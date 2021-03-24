package otomir23.connect.server.commands;

import otomir23.connect.server.Server;

import java.util.Arrays;

import static otomir23.connect.server.Server.LOGGER;

public class HelpCommand extends Command {
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            for (Command c: Server.getInstance().getServerCommands()) {
                LOGGER.log(c.getName() + ": " + c.getDescription());
            }
        } else {
            String command = args[0];
            boolean flag = false;
            for (Command c:Server.getInstance().getServerCommands()) {
                boolean flag1 = false;
                for (String alias : c.getAliases()) {
                    if (alias.equals(command)) {
                        flag1 = true;
                        break;
                    }
                }
                if (c.getName().equals(command) || flag1) {
                    flag = true;
                    LOGGER.log(c.getName() + ": " + c.getDescription());
                    LOGGER.log("Minimal args: " + c.getMinArgs() + ", Maximum args: " + (c.getMaxArgs() != Integer.MAX_VALUE ? c.getMaxArgs() : "Infinity") + ", Aliases: " + Arrays.toString(c.getAliases()));
                }

            }
            if (!flag) {
                throw new CommandException("Command not found");
            }
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "?",
                "commands"
        };
    }

    @Override
    public String getDescription() {
        return "Shows list of commands or information about specific one.";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }
}
