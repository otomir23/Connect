package otomir23.connect.client;

import otomir23.connect.util.Packet;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static final int PING_RATE = 1;

    public static String username = "Dev";
    public static long pingMills = 0;

    public static void main(String[] args) {
        System.out.println("Please enter your username: ");
        username = new Scanner(System.in).next();
        username.replaceAll(" ", "");
        username.replaceAll(":", "");
        username.replaceAll("/", "");
        username.replaceAll("\\\\", "");

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

            new Packet("connection",  username).send(socket.getOutputStream());
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        pingMills = System.currentTimeMillis();
                        new Packet("ping", String.valueOf(pingMills) ).send(socket.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, PING_RATE * 60 * 1000);

            // ========= INPUT THREAD =========
            Scanner inputScanner = new Scanner(System.in);
            Thread inputThread = new Thread(() -> {
                while (inputScanner.hasNextLine()) {
                    if (inputScanner.nextLine().equals("disconnect")) {
                        if (!socket.isClosed()) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    } else if (inputScanner.nextLine().equals("stop") || inputScanner.nextLine().equals("exit")) {
                        System.exit(0);
                    }
                }
            });
            inputThread.start();

            // ========= PACKET THREAD =========
            Thread packetThread = new Thread(() -> {
                try {
                    Scanner scanner = new Scanner(socket.getInputStream());
                    while (scanner.hasNextLine()) {
                        String message = scanner.nextLine();
                        Packet input = Packet.parsePacket(message);
                            switch (input.getKey()) {
                                case "pong":
                                    long ping = System.currentTimeMillis() - pingMills;
                                    System.out.println("Your ping is " + ping);
                                    break;
                                case "disconnect":
                                    socket.close();
                                    System.out.println("Disconnected. Reason: " + input.getValue());
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + input.getKey());
                            }
                        socket.getOutputStream().flush();
                    }
                } catch (IOException e) {
                    // do nothing
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }); packetThread.start();

            // ========= START =========
            while (!socket.isClosed()) {

            }

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            inputThread.stop();
            packetThread.stop();
            timer.cancel();

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            connect();
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
}
