package otomir23.connect.server;

import otomir23.connect.server.util.Logger;
import otomir23.connect.server.util.PropertiesManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    public volatile static Server instance;
    public final static Logger LOGGER = new Logger("Server");
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

    private volatile ArrayList<User> users;
    private PropertiesManager properties;
    private ServerSocket serverSocket;
    private Thread connectionHandler;
    private Thread inputHandler;

    Server() {
        properties = new PropertiesManager(new File("./server.properties"));
        Logger.setDebug(Boolean.parseBoolean(
                properties.getProperty("debug")
        ));
        String portValue = properties.getProperty("port");

        int port = Integer.parseInt(portValue);
        int maxConnections = Integer.parseInt(properties.getProperty("max"));

        users = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
            LOGGER.log("Server started on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());

            connectionHandler = new Thread(() -> {
                while (true) {
                    try {
                        while (true) {
                            if (users.size() < maxConnections) break;
                        }
                        Socket socket = serverSocket.accept();
                        LOGGER.debug("Got new connection from " + socket.getInetAddress().getHostAddress());
                        User c = new User(socket);
                        Thread thread = new Thread(c);
                        thread.start();
                        c.thread = thread;
                        users.add(c);
                    } catch (IOException ignored) {
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
            for (User c:
                    users) {
                LOGGER.debug("Shutting down " + c.getUsername() + " interaction thread...");
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

    public ArrayList<User> getConnectedUsers() {
        return new ArrayList<>(users);
    }

    public void disconnectUser(User user) throws IOException {
        if (!user.getClientSocket().isClosed()) user.getClientSocket().close();
        users.remove(this);
    }
}
