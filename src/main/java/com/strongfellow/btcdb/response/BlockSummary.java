package com.strongfellow.btcdb.response;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class BlockSummary {

    private String timestamp;
    private Long bits;
    private final List<String> children = new ArrayList<>();
    private String coinbaseScript;
    private Long coinbaseValue;
    private Integer height;
    private String merkle;
    private Long nonce;
    private Integer numTx;
    private String parent;
    private Long reward;
    private Integer size;
    private Long sumOfTxins;
    private Long sumOfTxouts;
    private Long version;

    public void addChild(String child) {
        this.children.add(child);
    }

    public List<String> getChildren() {
        return children;
    }

    public Long getBits() {
        return bits;
    }
    public String getCoinbaseScript() {
        return this.coinbaseScript;
    }
    public Long getCoinbaseValue() {
        return this.coinbaseValue;
    }

    public Long getFeesAvailable() {
        if (sumOfTxins != null && sumOfTxouts != null && coinbaseValue != null) {
            return sumOfTxins - (sumOfTxouts - coinbaseValue);
        } else {
            return null;
        }
    }

    public Long getFeesClaimed() {
        return coinbaseValue - getReward();
    }
    public Integer getHeight() {
        return this.height;
    }
    public String getMerkle() {
        return this.merkle;
    }

    public Long getNonce() {
        return nonce;
    }
    public Integer getNumTx() {
        return this.numTx;
    }
    public String getParent() {
        return parent;
    }
    public Long getReward() {
        return this.reward;
    }
    public Integer getSize() {
        return size;
    }
    public Long getSumOfTxins() {
        return this.sumOfTxins;
    }

    public Long getSumOfTxouts() {
        return this.sumOfTxouts;
    }

    public String getTimestamp() {
        return this.timestamp;
    }
    public Long getVersion() {
        return version;
    }
    public void setBits(Long bits) {
        this.bits = bits;
    }
    public void setCoinbaseScript(byte[] script) {
        this.coinbaseScript = new String(script);
    }
    public void setCoinbaseValue(Long v) {
        this.coinbaseValue = v;
    }
    public void setHeight(Integer h) {
        this.height = h;
        this.reward = 5000000000L;
        while (h >= 210000 && this.reward > 625000000L) {
            h -= 210000;
            this.reward = this.reward / 2;
        }
    }
    public void setMerkle(String merkle) {
        this.merkle = merkle;
    }
    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }
    public void setNumTx(int n) {
        this.numTx = n;
    }

    public void setParent(String p) {
        this.parent = p;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setSumOfTxins(Long value) {
        this.sumOfTxins = value;
    }

    public void setSumOfTxOuts(Long v) {
        this.sumOfTxouts = v;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = ISODateTimeFormat.dateTime().print(new DateTime(timestamp * 1000, DateTimeZone.UTC));
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
