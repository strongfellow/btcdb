package com.strongfellow.btcdb.components;

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

    public Integer size;
    public String timestamp;
    public Long bits;
    public Long version;
    public Long nonce;

    public Integer numTx;

    public String merkle;
    public String parent;
    public final List<String> children = new ArrayList<>();
    private Integer height;
    private Long reward = null;
    private Long sumOfTxouts;
    private Long sumOfTxins;
    private Long coinbaseValue;

    public Long getFeesClaimed() {
        return coinbaseValue - getReward();
    }

    public Long getFeesAvailable() {
        return sumOfTxins - (sumOfTxouts - coinbaseValue);
    }
}
