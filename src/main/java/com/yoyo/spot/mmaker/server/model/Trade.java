package com.yoyo.spot.mmaker.server.model;

import com.yoyo.spot.mmaker.server.enums.TradeDirection;

import java.math.BigDecimal;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/21
 **/

public class Trade {

    private Long uniqueTradeId;
    private String tradeId;
    private long timestamp;
    private BigDecimal price;
    private BigDecimal amount;
    private TradeDirection direction;

    public Long getUniqueTradeId() {
        return uniqueTradeId;
    }

    public void setUniqueTradeId(Long uniqueTradeId) {
        this.uniqueTradeId = uniqueTradeId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TradeDirection getDirection() {
        return direction;
    }

    public void setDirection(TradeDirection direction) {
        this.direction = direction;
    }
}
