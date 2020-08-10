package com.yoyo.spot.mmaker.server.model;

import java.util.List;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/21
 **/

public class TradeEvent {

    private String symbol;
    private Long timestamp;
    private List<com.yoyo.spot.mmaker.server.model.Trade> tradeList;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Trade> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<Trade> tradeList) {
        this.tradeList = tradeList;
    }
}
