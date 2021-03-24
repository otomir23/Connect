package otomir23.connect.server.util;

import otomir23.connect.server.User;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BanManager {
    private static final File file = new File("./ban.list");
    private static final Logger LOGGER = new Logger("BanManager");
    private final HashMap<String, String> bannedUsers = new HashMap<>();

    public BanManager() {
        try {
            if (!file.exists()) file.createNewFile();
            Scanner sc = new Scanner(file);
            ArrayList<String> banFileContent = new ArrayList<>();
            while (sc.hasNextLine()) {
                banFileContent.add(sc.nextLine());
            }

            for (String line : banFileContent) {
                String[] ban = line.split(":");

                if (ban.length != 2) {
                    continue;
                }
                ban[1] = ban[1].replaceFirst(" ", "");
                bannedUsers.put(ban[0], ban[1]);
            }
        } catch (IOException ioe) {
            LOGGER.fatal(ioe, -1);
        }
    }

    public void ban(User user) {
        ban(user, "Reason not specified.");
    }

    public void ban(User user, String reason) {
        if (!user.isConnectionConfirmed()) return;
        reason.replaceAll(":", "");
        String banNick = user.getUsername();
        user.disconnect("You got banned. Reason: " + reason);
        bannedUsers.put(banNick, reason);
        try {
            Files.write(Paths.get(file.getAbsolutePath()), (banNick + ": " + reason + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ioe) {
            LOGGER.fatal(ioe, -1);
        }
    }

    public void unban(String username) {
        bannedUsers.remove(username);
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
            String out = "";
            for (String s: bannedUsers.keySet()) {
                out += s + ": " + bannedUsers.get(s) + "\n";
            }
            Files.write(Paths.get(file.getAbsolutePath()), out.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ioe) {
            LOGGER.fatal(ioe, -1);
        }
    }

    public String isBanned(String username) {
        return bannedUsers.getOrDefault(username, null);
    }
}
