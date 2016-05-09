package com.strongfellow.btcdb.protocol;

public class Output {

    private final long value;
    private final byte[] script;
    
    public Output(long value, byte[] script) {
        this.value = value;
        this.script = script;
    }

    public long getValue() {
        return value;
    }

    public byte[] getScript() {
        return script;
    }
}
