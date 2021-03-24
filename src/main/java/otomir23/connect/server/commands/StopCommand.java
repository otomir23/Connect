package otomir23.connect.server.commands;

import otomir23.connect.server.Server;

public class StopCommand extends Command {
    @Override
    public void execute(String[] args) {
        Server.getInstance().stop();
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Stops the server.";
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
