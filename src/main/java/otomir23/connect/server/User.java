package otomir23.connect.server;

import otomir23.connect.server.util.Logger;
import otomir23.connect.util.Packet;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class User implements Runnable {
    private Socket clientSocket;
    private boolean connectionConfirmed = false;
    private String username = "_";
    private Logger LOGGER;
    public Thread thread;
    public Thread connectionThread;

    public User(Socket clientSocket) {
        this.clientSocket = clientSocket;
        LOGGER = Server.LOGGER;

        connectionThread = new Thread(() -> {
            while (true)
                if (clientSocket.isClosed()) {
                    disconnect();
                    break;
                }
        });
        connectionThread.start();
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                LOGGER.debug("New message: " + message);
                Packet input = Packet.parsePacket(message);
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
                            sendPacket(new Packet("pong", String.valueOf(ping)));
                            break;
                        case "disconnect":
                            disconnect(input.getValue());
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

    public void disconnect() {
        disconnect("Disconnected");
    }

    public void disconnect(String reason) {
        try {
            if (!getClientSocket().isClosed()) {
                sendPacket(new Packet("disconnect", reason));
                getClientSocket().close();
            }
            LOGGER.log(username + " disconnected. Reason: " + reason);
            thread.stop();
            connectionThread.stop();
            Server.instance.getConnectedUsers().remove(this);
        } catch (IOException ioe) {
            LOGGER.fatal(ioe, -1);
        }
    }

    public void sendPacket(Packet packet) throws IOException {
        packet.send(clientSocket.getOutputStream());
    }

    public String getUsername() {
        return username;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public boolean isConnectionConfirmed() {
        return connectionConfirmed;
    }
}
