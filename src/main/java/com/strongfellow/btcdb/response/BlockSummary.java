package com.strongfellow.btcdb.response;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class BlockSummary {

    public void setHeight(Integer h) {
        this.height = h;
        this.reward = 5000000000L;
        while (h >= 210000 && this.reward > 625000000L) {
            h -= 210000;
            this.reward = this.reward / 2;
        }
    }

    public Integer getHeight() {
        return this.height;
    }

    public Long getReward() {
        return this.reward;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = ISODateTimeFormat.dateTime().print(new DateTime(timestamp * 1000, DateTimeZone.UTC));
    }

    public void setSumOfTxins(Long value) {
        this.sumOfTxins = value;
    }

    public void setSumOfTxOuts(Long v) {
        this.sumOfTxouts = v;
    }

    public Long getSumOfTxouts() {
        return this.sumOfTxouts;
    }

    public Long getSumOfTxins() {
        return this.sumOfTxins;
    }

    public void setCoinbaseValue(Long v) {
        this.coinbaseValue = v;
    }

    public Long getCoinbaseValue() {
        return this.coinbaseValue;
    }

    public void setCoinbaseScript(byte[] script) {
        this.coinbaseScript = new String(script);
    }

    public String getCoinbaseScript() {
        return this.coinbaseScript;
    }

    private String coinbaseScript;
    private Integer size;
    public String timestamp;
    private Long bits;
    private Long version;
    private Long nonce;

    private Integer numTx;

    private String merkle;
    private String parent;
    public String getParent() {
        return parent;
    }

    private final List<String> children = new ArrayList<>();
    private Integer height;
    private Long reward = null;
    private Long sumOfTxouts;
    private Long sumOfTxins;
    private Long coinbaseValue;

    public Long getFeesClaimed() {
        return coinbaseValue - getReward();
    }

    public Long getFeesAvailable() {
        if (sumOfTxins != null && sumOfTxouts != null && coinbaseValue != null) {
            return sumOfTxins - (sumOfTxouts - coinbaseValue);
        } else {
            return null;
        }
    }
    public Integer getSize() {
        return size;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
    public Long getBits() {
        return bits;
    }
    public void setBits(Long bits) {
        this.bits = bits;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
    public Long getNonce() {
        return nonce;
    }
    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public Integer getNumTx() {
        return this.numTx;
    }

    public void setMerkle(String merkle) {
        this.merkle = merkle;
    }

    public String getMerkle() {
        return this.merkle;
    }

    public void setNumTx(int n) {
        this.numTx = n;
    }

    public void setParent(String p) {
        this.parent = p;
    }

    public void addChild(String child) {
        this.children.add(child);
    }
}
