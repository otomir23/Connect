package otomir23.connect.server.commands;

import otomir23.connect.server.Server;
import otomir23.connect.server.User;
import otomir23.connect.server.util.BanManager;

import java.util.Arrays;

public class BanCommand extends Command {
    @Override
    public void execute(String[] args) {
        User user = Server.getInstance().getUser(args[0]);
        String reason = "Banned";
        if (args.length > 1) {
            reason = "";
            for (String s: Arrays.copyOfRange(args, 1, args.length)) {
                reason += s + " ";
            }
        }
        if (user != null) BanManager.ban(user, reason);
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
    public String getDescription() {
        return "Disconnects user from server add adds him to blacklist.";
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
