package otomir23.connect.server;

import otomir23.connect.server.commands.*;
import otomir23.connect.server.util.BanManager;
import otomir23.connect.server.util.Logger;
import otomir23.connect.server.util.PropertiesManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
    private volatile static Server instance;
    public final static Logger LOGGER = new Logger("Server");
    private static Thread handler;
    private static volatile boolean running = true;
    private static boolean stopping = true;

    public static void main(String[] args) {
        handler = new Thread(Server::run);
        handler.start();
    }

    public static Server getInstance() {
        return instance;
    }

    private static void run() {
        while (true) {
            running = true;
            instance = new Server();
            while (running) {
            }
            LOGGER.debug("vvv");
            instance.stop();
            if (stopping) {
                System.exit(0);
            }
        }
    }

    private final ArrayList<Command> serverCommands = new ArrayList<>();
    private int maxConnections;
    private int port;
    private BanManager banManager;
    private volatile ArrayList<User> users;
    private PropertiesManager properties;
    private ServerSocket serverSocket;
    private Thread connectionHandler;
    private Thread inputHandler;

    Server() {
        // PROPERTIES
        properties = new PropertiesManager(new File("./server.properties"));
        Logger.setDebug(Boolean.parseBoolean(
                properties.getProperty("debug")
        ));
        LOGGER.debug("DEBUG MODE IS ENABLED");
        String portValue = properties.getProperty("port");

        port = Integer.parseInt(portValue);
        maxConnections = Integer.parseInt(properties.getProperty("max"));


        // COMMANDS
        serverCommands.addAll(Arrays.asList(new BanCommand(),
                new KickCommand(),
                new HelpCommand(),
                new ListCommand(),
                new UnbanCommand()));
        banManager = new BanManager();

        // SOCKET
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
                while (input.hasNextLine()) {
                    String inputText = input.nextLine();
                    String[] split = inputText.split(" ");
                    String command = split[0];
                    String[] args = Arrays.copyOfRange(split, 1, split.length);
                    LOGGER.debug("Executing command " + command + " With given args " + Arrays.toString(args));
                    try {
                        boolean flag = false;
                        for (Command c:serverCommands) {
                            boolean flag1 = false;
                            for (String alias : c.getAliases()) {
                                if (alias.equals(command)) {
                                    flag1 = true;
                                    break;
                                }
                            }
                            if (c.getName().equals(command) || flag1) {
                                flag = true;
                                if (args.length <= c.getMaxArgs() && args.length >= c.getMinArgs())
                                    c.execute(args);
                                else if (args.length < c.getMinArgs())
                                    LOGGER.log("Command " + c.getName() + " requires at least " + c.getMinArgs() + " arguments");
                                else if (args.length > c.getMaxArgs())
                                    LOGGER.log("Command " + c.getName() + " requires no more than " + c.getMaxArgs() + " arguments");
                            }

                        }
                        if (!flag) {
                            LOGGER.log("Command \"" + command + "\" not found");
                        }
                    } catch (CommandException e) {
                        LOGGER.log("Command execution failed: " + e.getMessage());
                    }
                }
            });

            connectionHandler.start();
            inputHandler.start();

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

    /*public void onConsoleCommand(String command, String[] args) {
        LOGGER.debug(command);
        LOGGER.debug(Arrays.toString(args));
        switch (command) {
            case "stop":
                LOGGER.log("Stopping server...");
                running = false;
                stopping = true;
                break;
            case "restart":
                LOGGER.log("Server is restarting...");
                running = false;
                break;
            default:
                throw new CommandException("");
        }
    }*/

    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }

    public ArrayList<User> getConnectedUsers() {
        ArrayList<User> userArrayList = new ArrayList<>();
        for (User user: users) {
            if (user.isConnectionConfirmed()) userArrayList.add(user);
        }
        return userArrayList;
    }

    public ArrayList<User> getAllUsers() {
        return users;
    }

    public int getMaxUsers() {
        return maxConnections;
    }

    public ArrayList<Command> getServerCommands() {
        return new ArrayList<>(serverCommands);
    }

    public void ban(User user) {
        banManager.ban(user);
    }

    public void ban(User user, String reason) {
        banManager.ban(user, reason);
    }

    public void unban(String username) {
        banManager.unban(username);
    }

    public String isBanned(String username) {
        return banManager.isBanned(username);
    }
}
