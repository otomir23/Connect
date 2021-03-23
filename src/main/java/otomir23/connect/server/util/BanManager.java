package otomir23.connect.server.util;

import otomir23.connect.server.User;

public class BanManager {
    public static void ban(User user) {
        ban(user, "Reason not specified.");
    }

    public static void ban(User user, String reason) {
        if (!user.isConnectionConfirmed()) return;
        String banNick = user.getUsername();
        user.disconnect("You got banned. Reason: " + reason);
    }

    public static void unban(String username) {
    }
}
