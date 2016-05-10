package com.strongfellow.btcdb.protocol;

public class Metadata {
    
    private final int size;
    private final byte[] hash;

    public Metadata(int size, byte[] hash) {
        this.size = size;
        this.hash = hash;
    }

    public int getSize() {
        return size;
    }

    public byte[] getHash() {
        return hash;
    }
}
