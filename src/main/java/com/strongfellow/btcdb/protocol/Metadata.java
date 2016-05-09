package com.strongfellow.btcdb.protocol;

public class Metadata {
    
    private final int size;
    private final String headerHash;

    public Metadata(int size, String headerHash) {
        this.size = size;
        this.headerHash = headerHash;
    }

    public int getSize() {
        return size;
    }

    public String getHash() {
        return headerHash;
    }
}
