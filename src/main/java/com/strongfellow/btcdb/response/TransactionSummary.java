package com.strongfellow.btcdb.response;

public class TransactionSummary {

    private Long outputValue;
    private Long fee;
    private Long size;
    private Long lockTime;
    private Long version;

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
}
