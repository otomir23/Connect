package otomir23.connect.server.commands;

public abstract class Command {
    public abstract void execute(String[] args);
    public abstract String getName();
    public abstract String[] getAliases();
    public abstract int getMinArgs();
    public abstract int getMaxArgs();
}
