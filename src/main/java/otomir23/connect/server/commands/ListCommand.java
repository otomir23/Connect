package otomir23.connect.server.commands;

import otomir23.connect.server.Server;
import otomir23.connect.server.User;

import java.util.ArrayList;
import java.util.Arrays;

public class ListCommand extends Command {
    @Override
    public void execute(String[] args) {
        ArrayList<User> users = Server.getInstance().getConnectedUsers();
        if (users.size() == 0) {
            Server.LOGGER.log("There is no connected users.");
            return;
        }
        StringBuilder userString = new StringBuilder("Users connected (" + users.size() + "/" + Server.getInstance().getMaxUsers() + "): ");
        for (User user : users) {
            userString.append(user.getUsername()).append(", ");
        }
        userString = new StringBuilder(new String(Arrays.copyOfRange(userString.toString().toCharArray(), 0, userString.toString().toCharArray().length - 2)));
        Server.LOGGER.log(userString.toString());
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Shows list of all connected users.";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 0;
    }
}
