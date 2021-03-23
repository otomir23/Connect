package otomir23.connect.util;

public class Packet<K,V> {

    private final K key;
    private final V value;

    public Packet(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }

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