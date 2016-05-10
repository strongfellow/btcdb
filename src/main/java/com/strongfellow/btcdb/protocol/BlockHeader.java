package com.strongfellow.btcdb.protocol;

public class BlockHeader {
    
    private final int version;
    private final byte[] previousBlock;
    private final byte[] merkleRoot;
    private final long timestamp;
    private final long bits;
    private final long nonce;
    
    public BlockHeader(int version,
            byte[] previousBlock,
            byte[] merkleRoot,
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

    public byte[] getPreviousBlock() {
        return previousBlock;
    }

    public byte[] getMerkleRoot() {
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

}
