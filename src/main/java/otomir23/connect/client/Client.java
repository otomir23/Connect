package otomir23.connect.client;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Client {
    public static final int PING_RATE = 1;

    public static String username = "Dev";

    public static void main(String[] args) {
        while (true) {
            System.out.println("Please enter your username: ");
            username = new Scanner(System.in).next();

            if (!username.contains(" ") && !username.isEmpty()) break;
            System.out.println("Invalid username. Please enter valid one.");
        }
        connect();
    }

    /**
     * Connects user to the server
     * */
    public static void connect() {
        Socket socket = new Socket();
        InetSocketAddress target = null;

        while (target == null) {
            try {
                target = inputConnectionTarget();
            } catch (UnknownHostException | NumberFormatException | InputMismatchException e) {
                System.out.println("IP address or/and port is invalid. Please, enter it again.");
            }
        }

        System.out.println("Connecting to the server...");

        try {
            socket.connect(target);
            System.out.println("Connected to the server! [" + socket.getLocalPort() + "]");

            sendPacket(socket.getOutputStream(), "connection",  username);
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        sendPacket(socket.getOutputStream(), "ping", String.valueOf(System.currentTimeMillis()) );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, PING_RATE * 60 * 1000);

            // ========= START =========
            while (true) {
                if (new Scanner(System.in).next().equals("stop")) {
                    if (!socket.isClosed()) socket.close();
                    break;
                }
            }
        } catch (SocketException se) {
            System.err.println("Unable to connect to the server. Maybe IP is invalid, server is down, or you don't have internet connection.");

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            connect();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception ignored) {

        }
    }

    /**
     * Parses IP and port input and returns it through ConnectionData class.
     * @return Parsed IP and port in ConnectionData instance.
     * @throws UnknownHostException  if IP is invalid.
     * @throws InputMismatchException  if input is invalid.
     * */
    static InetSocketAddress inputConnectionTarget() throws UnknownHostException, InputMismatchException {
        int port = 26008;
        String IP;
        boolean portInput = true;

        System.out.println("Please enter IP address: ");
        IP = new Scanner(System.in).next();

        if (IP.contains(":"))
            if (IP.split(":").length == 2) {
                port = Integer.parseInt(IP.split(":")[1]);
                IP = IP.split(":")[0];
                portInput = false;
            }

        if (portInput) {
            System.out.println("Please enter port: ");
            port = new Scanner(System.in).nextInt();
        }


        return new InetSocketAddress(IP, port);
    }

    static void sendPacket(OutputStream out, String key, String value) throws IOException {
        out.write((key + ":" + value + "\n").getBytes());
        out.flush();
    }
}
