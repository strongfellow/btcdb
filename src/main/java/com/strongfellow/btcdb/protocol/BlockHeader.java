package com.strongfellow.btcdb.protocol;

public class BlockHeader {
    
    public BlockHeader(int version,
            String previousBlock,
            String merkleRoot,
            long timestamp,
            long bits,
            long nonce) {
        this.version = version;
        this.previousBlock = previousBlock;
        this.merkleRoot = merkleRoot;
        this.timestamp = timestamp;
        this.bits = bits;
        this.nonce = nonce;
    }
    
    public int getVersion() {
        return version;
    }

    public String getPreviousBlock() {
        return previousBlock;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getBits() {
        return bits;
    }

    public long getNonce() {
        return nonce;
    }

    private final int version;
    private final String previousBlock;
    private final String merkleRoot;
    private final long timestamp;
    private final long bits;
    private final long nonce;
}
