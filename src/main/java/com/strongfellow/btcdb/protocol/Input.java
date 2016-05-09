package com.strongfellow.btcdb.protocol;

public class Input {
    private final String hash;
    private final int index;
    private final byte[] script;
    private final long sequence;
    
    public Input(String hash, int index, byte[] script, long sequence) {
        this.hash= hash;
        this.index = index;
        this.script = script;
        this.sequence = sequence;
    }

    public String getHash() {
        return hash;
    }

    public int getIndex() {
        return index;
    }

    public byte[] getScript() {
        return script;
    }

    public long getSequence() {
        return sequence;
    }
}
