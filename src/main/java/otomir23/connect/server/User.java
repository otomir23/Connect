package otomir23.connect.server;

import otomir23.connect.server.util.Logger;
import otomir23.connect.server.util.Pair;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class User implements Runnable {
    private Socket clientSocket;
    private boolean connectionConfirmed = false;
    private String username = "_";
    private Logger LOGGER;
    public Thread thread;

    public User(Socket clientSocket) {
        this.clientSocket = clientSocket;
        LOGGER = Server.LOGGER;

        new Thread(() -> {
            while (true)
                if (clientSocket.isClosed()) {
                    LOGGER.log(username + " disconnected.");
                    try {
                        Server.instance.disconnectUser(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                            long ping = System.currentTimeMillis() - Long.parseLong(input.getValue());
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

    public String getUsername() {
        return username;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    //private useful methods
    private static Pair<String, String> parseInput(String input) {
        String[] inputString = input.split(":");
        if (inputString.length != 2) throw new IllegalArgumentException("Invalid input string.");
        return new Pair<>(inputString[0], inputString[1]);
    }
}
