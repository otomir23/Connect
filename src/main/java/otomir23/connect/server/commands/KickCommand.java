package otomir23.connect.server.commands;

import otomir23.connect.server.Server;
import otomir23.connect.server.User;

import java.util.Arrays;

public class KickCommand extends Command {
    @Override
    public void execute(String[] args) {
        User user = Server.getInstance().getUser(args[0]);
        String reason = "Kicked";
        if (args.length > 1) {
            reason = "";
            for (String s: Arrays.copyOfRange(args, 1, args.length)){
                reason += s + " ";
            }
        }
        if (user != null) user.disconnect(reason);
        else throw new CommandException("User not found");
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Disconnects user from server. User will able to reconnect.";
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
