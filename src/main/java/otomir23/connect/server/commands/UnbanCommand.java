package otomir23.connect.server.commands;

import otomir23.connect.server.Server;
import otomir23.connect.server.User;

import java.util.Arrays;

public class UnbanCommand extends Command {
    @Override
    public void execute(String[] args) {
        if (Server.getInstance().isBanned(args[0]) != null) {
            Server.getInstance().unban(args[0]);
            Server.LOGGER.log("User " + args[0] + " unbanned.");
        }
        else throw new CommandException("User " + args[0] + " is not banned");
    }

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pardon"};
    }

    @Override
    public String getDescription() {
        return "Removes user from blacklist.";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }
}
