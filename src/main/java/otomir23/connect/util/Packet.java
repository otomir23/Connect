package otomir23.connect.util;

import java.io.IOException;
import java.io.OutputStream;

public class Packet {
    private static final String split = " => ";

    private final String key;
    private final String value;

    public static Packet parsePacket(String s) {
        String[] inputString = s.split(split);
        if (inputString.length != 2) throw new IllegalArgumentException("Invalid input string.");
        return new Packet(inputString[0], inputString[1]);
    }
    public Packet(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() { return key; }
    public String getValue() { return value; }

    public void send(OutputStream stream) throws IOException {
        stream.write((key + split + value + "\n").getBytes());
        stream.flush();
    }

    @Override
    public int hashCode() { return key.hashCode() ^ value.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Packet)) return false;
        Packet pairo = (Packet) o;
        return this.key.equals(pairo.getKey()) &&
                this.value.equals(pairo.getValue());
    }

}