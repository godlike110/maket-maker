package com.yoyo.spot.mmaker.server.model;

import com.yoyo.spot.mmaker.server.config.CategoryConfig;

/**
 *
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/18
 **/
public class DataEngine {

    private String symbol;
    private Deph deph;
    private Boolean depthUpdated;
    private long lastDepthTime;
    private com.yoyo.spot.mmaker.server.model.TradeEvent tradeEvent;
    private Boolean tradeUpdated;
    private long lastTradeTime;
    private AccountStatus status = new AccountStatus();
    private CategoryConfig config;


    public CategoryConfig getConfig() {
        return config;
    }

    public void setConfig(CategoryConfig config) {
        this.config = config;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Deph getDeph() {
        return deph;
    }

    public void setDeph(Deph deph) {
        this.deph = deph;
    }

    public Boolean getDepthUpdated() {
        return depthUpdated;
    }

    public void setDepthUpdated(Boolean depthUpdated) {
        this.depthUpdated = depthUpdated;
    }

    public long getLastDepthTime() {
        return lastDepthTime;
    }

    public void setLastDepthTime(long lastDepthTime) {
        this.lastDepthTime = lastDepthTime;
    }

    public TradeEvent getTradeEvent() {
        return tradeEvent;
    }

    public void setTradeEvent(TradeEvent tradeEvent) {
        this.tradeEvent = tradeEvent;
    }

    public Boolean getTradeUpdated() {
        return tradeUpdated;
    }

    public void setTradeUpdated(Boolean tradeUpdated) {
        this.tradeUpdated = tradeUpdated;
    }

    public long getLastTradeTime() {
        return lastTradeTime;
    }

    public void setLastTradeTime(long lastTradeTime) {
        this.lastTradeTime = lastTradeTime;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
