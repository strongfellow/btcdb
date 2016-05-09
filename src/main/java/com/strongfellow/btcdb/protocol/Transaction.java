package com.strongfellow.btcdb.protocol;

import java.util.List;

public class Transaction {

    private final Metadata metadata;
    private final int version;
    private final List<Input> inputs;
    private final List<Output> outputs;
    private final long nLockTime;

    public Transaction(Metadata metadata, int version, List<Input> inputs, List<Output> outputs, long nLockTime) {
        this.metadata = metadata;
        this.version = version;
        this.inputs = inputs;
        this.outputs = outputs;
        this.nLockTime = nLockTime;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public int getVersion() {
        return version;
    }

    public long getnLockTime() {
        return nLockTime;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

}
