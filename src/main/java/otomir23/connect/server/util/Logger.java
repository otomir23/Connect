package otomir23.connect.server.util;

import java.util.Date;

public class Logger {
    private static boolean debug = false;

    public String name;

    public Logger(String name) {
        this.name = name;
    }

    private void print(String level, String text) {
        print(level, text, "");
    }

    private void print(String level, String text, String color) {
        Date date = new Date();
        String time = formatDate(date.getHours()) + ":" + formatDate(date.getMinutes()) + ":" + formatDate(date.getSeconds());
        System.out.println(color + "[" + time + "] [" + name + "/" + level + "] " + text + "\u001B[0m");
    }

    public void log(String text) {
        print("INFO", text);
    }

    public void warn(String text) {
        print("WARN", text, "\u001B[33m");
    }

    public void error(String text) {
        print("ERROR", text, "\u001B[31m");
    }

    public void error(Exception e) {
        StringBuilder text = new StringBuilder();
        text.append(e.getMessage()).append("\n");
        for (int i = e.getStackTrace().length - 2; i > -1; i--) {
            text.append(e.getStackTrace()[i]).append("\n");
        }
        error(text.toString());
    }

    public void fatal(String text, int status) {
        error(text);
        System.exit(status);
    }

    public void fatal(Exception e, int status) {
        error(e);
        System.exit(status);
    }

    public void debug(String text) {
        if (debug) print("DEBUG", text, "\u001B[32m");
    }

    public static void setDebug(boolean value) {
        debug = value;
    }

    private String formatDate(int rawDate) {
        String date = rawDate + "";
        if (date.toCharArray().length == 1) {
            return "0" + date;
        } else {
            return date;
        }
    }
}
