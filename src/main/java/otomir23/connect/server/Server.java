package otomir23.connect.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
    public volatile static Server instance;
    private static Thread handler;
    private static volatile boolean running = true;
    private static boolean stopping = true;

    public static void main(String[] args) {
        handler = new Thread(() -> {
            while (true) {
                running = true;
                instance = new Server();
                LOGGER.debug("vvv");
                instance.stop();
                if (stopping) {
                    System.exit(0);
                }
            }
        });
        handler.start();
    }

    private volatile ArrayList<Client> clients;
    private PropertiesReader properties;
    private final static Logger LOGGER = new Logger("Server");
    private ServerSocket serverSocket;
    private Thread connectionHandler;
    private Thread inputHandler;

    Server() {
        properties = new PropertiesReader(new File("./server.properties"));
        Logger.setDebug(Boolean.parseBoolean(
                properties.getProperty("debug")
        ));
        String portValue = properties.getProperty("port");
        portValue = "26008"; //TODO
        LOGGER.log(portValue);

        int port = Integer.parseInt(portValue);
        //int maxConnections = Integer.parseInt(properties.getProperty("max"));
        int maxConnections = 12; //TODO

        clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
            LOGGER.log("Server started on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());

            connectionHandler = new Thread(() -> {
                while (true) {
                    try {
                        while (true) {
                            if (clients.size() < maxConnections) break;
                        }
                        Socket socket = serverSocket.accept();
                        LOGGER.debug("Got new connection from " + socket.getInetAddress().getHostAddress());
                        Client c = new Client(socket);
                        Thread thread = new Thread(c);
                        thread.start();
                        c.thread = thread;
                        clients.add(c);
                    } catch (IOException e) {
                        //LOGGER.error(e);
                        //I'm gonna fucking ignore exceptions
                    }
                }
            });



            inputHandler = new Thread(() -> {
                Scanner input = new Scanner(System.in);
                while (true) {
                    if (input.hasNext()) {
                        String command = input.next();
                        if (command.equals("stop")) {
                            LOGGER.log("Stopping server...");
                            running = false;
                            stopping = true;
                        } else if (command.equals("restart")) {
                            LOGGER.log("Server is restarting...");
                            running = false;
                        } else {
                            LOGGER.log("Unknown command!");
                        }
                    }
                }
            });

            connectionHandler.start();
            inputHandler.start();

            while (running) {}

        } catch (IOException e) {
            LOGGER.fatal(e, -1);
        }
    }

    public void stop() {
        try {
            LOGGER.debug("stop");

            LOGGER.debug("Shutting down connection thread...");
            connectionHandler.stop();
            LOGGER.debug("Connection thread is shut down");

            LOGGER.debug("Shutting down all client interaction threads...");
            for (Client c:
                 clients) {
                LOGGER.debug("Shutting down " + c.username + " interaction thread...");
                c.thread.stop();
            }
            LOGGER.debug("All client interaction threads are shut down");

            LOGGER.debug("Shutting down console input thread...");
            inputHandler.stop();
            LOGGER.debug("Console input thread is shut down");

            LOGGER.debug("Closing socket...");
            serverSocket.close();
            LOGGER.debug("Socket closed");

            LOGGER.debug("Server stopped successful");
            LOGGER.log("Server stopped.");
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static class Client implements Runnable {
        private Socket clientSocket;
        private boolean connectionConfirmed = false;
        private String username = "_";
        private Logger LOGGER;
        public Thread thread;

        public Client(Socket clientSocket) {
            this.clientSocket = clientSocket;
            LOGGER = Server.LOGGER;

            new Thread(() -> {
                while (true)
                if (clientSocket.isClosed()) {
                    LOGGER.log(username + " disconnected.");
                    Server.instance.clients.remove(this);
                    thread.stop();
                    Thread.currentThread().stop();
                }
            }).start();
        }

        @Override
        public void run() {
            try {
                Scanner scanner = new Scanner(clientSocket.getInputStream());
                while (scanner.hasNextLine()) {
                    String message = scanner.nextLine();
                    LOGGER.debug("New message: " + message);
                    Pair<String, String> input = parseInput(message);
                    if (input.getKey().equals("connection")) {
                        if (!connectionConfirmed) {
                            username = input.getValue();
                            LOGGER.log(username + " connected. [" + clientSocket.getInetAddress().getHostAddress() + "]");
                            connectionConfirmed = true;
                        } else {
                            LOGGER.warn(username + " is already connected, but sends connect packet again.");
                        }
                    } else if (connectionConfirmed) {
                        switch (input.getKey()) {
                            case "ping":
                                long ping = System.currentTimeMillis() - Long.parseLong( input.getValue() );
                                LOGGER.debug(username + "'s ping is " + ping + " ms");
                                sendPacket("pong", String.valueOf(ping));
                                break;
                            case "penis":
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + input.getKey());
                        }
                    }
                    clientSocket.getOutputStream().flush();
                }
                clientSocket.close();
            } catch (IOException e) {
                // do nothing
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }

        void sendPacket(String key, String value) throws IOException {
            clientSocket.getOutputStream().write((key + ":" + value + "\n").getBytes());
            clientSocket.getOutputStream().flush();
        }

        static Pair<String,String> parseInput(String input) {
            String[] inputString = input.split(":");
            if (inputString.length != 2) throw new IllegalArgumentException("Invalid input string.");
            return new Pair<>(inputString[0], inputString[1]);
        }
    }
}
