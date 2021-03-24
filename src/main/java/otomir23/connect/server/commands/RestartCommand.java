package otomir23.connect.server.commands;

import otomir23.connect.server.Server;

public class RestartCommand extends Command {
    @Override
    public void execute(String[] args) {
        Server.getInstance().restart();
    }

    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Restarts the server.";
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
