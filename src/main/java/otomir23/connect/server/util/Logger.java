package otomir23.connect.server.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Random;

public class Logger {
    private static File file = null;
    private static boolean debug = false;
    private static final Logger LOGGER = new Logger("Logger");

    public String name;

    public Logger(String name) {
        this.name = name;
        prepareFile();
    }

    private void print(String level, String text) {
        print(level, text, "");
    }

    private void print(String level, String text, String color) {
        Date date = new Date();
        String time = formatDate(date.getHours()) + ":" + formatDate(date.getMinutes()) + ":" + formatDate(date.getSeconds());
        String line = color + "[" + time + "] [" + name + "/" + level + "] " + text + "\u001B[0m";
        System.out.println(line);
        writeToFile(line);
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

    public void input(String input) {
        writeToFile("> " + input);
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

    private static void prepareFile() {
        if (file != null) return;
        try {
            File dir = new File("logs/");
            if (!dir.exists()) dir.mkdir();

            file = new File("logs/latest.log");
            if (file.exists()) {
                file.renameTo(new File("logs/" + ("date-" + Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toString().replaceAll(":", "-").replaceFirst("T", "-time-").split("\\.")[0]) + ".log"));
                file = new File("logs/latest.log");
            }
            file.createNewFile();
        } catch (IOException ioe) {
            //LOGGER.fatal(ioe, -1);
            ioe.printStackTrace();
        }
    }

    private static void writeToFile(String s) {
        if (file == null) prepareFile();
        try {
            Files.write(Paths.get(file.getAbsolutePath()), (s + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ioe) {
            //LOGGER.fatal(ioe, -1);
            ioe.printStackTrace();
        }
    }
}
