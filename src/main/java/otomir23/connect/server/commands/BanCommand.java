package otomir23.connect.server.commands;

import otomir23.connect.server.Server;
import otomir23.connect.server.User;
import otomir23.connect.server.util.BanManager;

public class BanCommand extends Command {
    @Override
    public void execute(String[] args) {
        User user = Server.instance.getUser(args[0]);
        if (user != null) BanManager.ban(user);
        else throw new CommandException("User not found");
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return Integer.MAX_VALUE;
    }
}