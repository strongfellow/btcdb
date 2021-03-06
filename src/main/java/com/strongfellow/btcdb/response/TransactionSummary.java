package com.strongfellow.btcdb.response;

import java.util.ArrayList;
import java.util.List;

public class TransactionSummary {

    private String hash;
    private Long outputValue;
    private Long fee;
    private Long size;
    private Long lockTime;
    private Long version;
    private List<Txin> inputs = new ArrayList<>();
    private List<Txout> outputs = new ArrayList<>();
    private List<BlockPointer> blockPointers;

    public List<Txin> getInputs() {
        return inputs;
    }

    public Long getFees() {
        long result = 0;
        for (Txin t : this.inputs) {
            result += t.getValue();
        }
        for (Txout t : this.outputs) {
            result -= t.getValue();
        }
        return result;
    }

    public List<Txout> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Txout> outputs) {
        this.outputs = outputs;
    }

    public void setInputs(List<Txin> inputs) {
        this.inputs = inputs;
    }

    public Long getOutputValue() {
        return outputValue;
    }
    public void setOutputValue(Long outputValue) {
        this.outputValue = outputValue;
    }
    public Long getFee() {
        return fee;
    }
    public void setFee(Long fee) {
        this.fee = fee;
    }
    public Long getSize() {
        return size;
    }
    public void setSize(Long size) {
        this.size = size;
    }
    public Long getLockTime() {
        return lockTime;
    }
    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setBlockPointers(List<BlockPointer> blocks) {
        this.blockPointers = blocks;
    }
    public List<BlockPointer> getBlockPointers() {
        return this.blockPointers;
    }

}
