package com.yoyo.spot.mmaker.server.config;

import java.math.BigDecimal;

/**
 * 策略配置
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/18
 **/

public class CategoryConfig {

    private String thirdEx = "huobi";

    /**
     * 刷新价格 灵敏度
     */
    private int sensitivity = 10;

    /**
     * 是否是单机币
     */
    private boolean isSingle = false;

    /**
     * 是否关闭交易对做市
     */
    private boolean isDown = false;

    /**
     * 与第三方参照火币价格比
     */
    private BigDecimal priceRate = new BigDecimal(1);


    /**
     * 第三方交易对
     */
    private String thirdSymbol = "btcusdt";

    /**
     * 标的币
     */
    private String assetSymbol = "BTC";

    /**
     * 交易对名称
     */
    private String mmSymbol = "BTC/USDT";

    /**
     * 单边挂单数
     */
    private int mmLevels = 10;

    /**
     * 下单精度
     */
    private int priceFormatter = 2;

    /**
     * 下单精度
     */
    private int amountFormatter = 6;

    /**
     * 对冲费用
     */
    private BigDecimal hedgeFee = new BigDecimal("0.002");

    /**
     * 做市买卖价差相对于价格的比例
     */
    private BigDecimal mmDistance = new BigDecimal("0.0005");

    /**
     * 最大允许的做市买卖价差
     */
    private BigDecimal maxDistance = new BigDecimal("0.003");

    /**
     * 最小允许的做市买卖价差
     */
    private BigDecimal minDistance = new BigDecimal("0.0");

    /**
     * 每级挂单之间的距离
     */
    private BigDecimal levelDistance = new BigDecimal("0.0002");

    /**
     * 做市的挂单量
     */
    private BigDecimal amount = new BigDecimal("0.05");

    /**
     * 最小下单数量
     */
    private BigDecimal minAmount = new BigDecimal("0.01");

    /**
     * 最大允许的单边仓位
     */
    private BigDecimal maxPosition = new BigDecimal("0.5");

    /**
     * 每次对冲下单量
     */
    private BigDecimal hedgeUnit = new BigDecimal("0.05");

    /**
     * 刷量相对于目标交易所(火币)的比例
     */
    private BigDecimal tradeAmountRatio = new BigDecimal("0.3");

    /**
     * 交易价格相对火币的交易所做正态随机扰动，sigma值
     */
    private BigDecimal tradePriceSigmaRatio = new BigDecimal("0.0002");

    /**
     * 每档挂单量做正态随机扰动
     */
    private BigDecimal amountSigmaRatio = new BigDecimal("0.9");

    /**
     * 每档价格做正态随机扰动
     */
    private BigDecimal priceSigmaRatio = new BigDecimal("0.1");

    public boolean isDown() {
        return isDown;
    }

    public void setDown(boolean down) {
        isDown = down;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public String getThirdEx() {
        return thirdEx;
    }

    public void setThirdEx(String thirdEx) {
        this.thirdEx = thirdEx;
    }

    public BigDecimal getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(BigDecimal priceRate) {
        this.priceRate = priceRate;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    public String getThirdSymbol() {
        return thirdSymbol;
    }

    public void setThirdSymbol(String thirdSymbol) {
        this.thirdSymbol = thirdSymbol;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }

    public String getMmSymbol() {
        return mmSymbol;
    }

    public void setMmSymbol(String mmSymbol) {
        this.mmSymbol = mmSymbol;
    }

    public int getMmLevels() {
        return mmLevels;
    }

    public void setMmLevels(int mmLevels) {
        this.mmLevels = mmLevels;
    }

    public int getPriceFormatter() {
        return priceFormatter;
    }

    public void setPriceFormatter(int priceFormatter) {
        this.priceFormatter = priceFormatter;
    }

    public int getAmountFormatter() {
        return amountFormatter;
    }

    public void setAmountFormatter(int amountFormatter) {
        this.amountFormatter = amountFormatter;
    }

    public BigDecimal getHedgeFee() {
        return hedgeFee;
    }

    public void setHedgeFee(BigDecimal hedgeFee) {
        this.hedgeFee = hedgeFee;
    }

    public BigDecimal getMmDistance() {
        return mmDistance;
    }

    public void setMmDistance(BigDecimal mmDistance) {
        this.mmDistance = mmDistance;
    }

    public BigDecimal getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(BigDecimal maxDistance) {
        this.maxDistance = maxDistance;
    }

    public BigDecimal getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(BigDecimal minDistance) {
        this.minDistance = minDistance;
    }

    public BigDecimal getLevelDistance() {
        return levelDistance;
    }

    public void setLevelDistance(BigDecimal levelDistance) {
        this.levelDistance = levelDistance;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(BigDecimal maxPosition) {
        this.maxPosition = maxPosition;
    }

    public BigDecimal getHedgeUnit() {
        return hedgeUnit;
    }

    public void setHedgeUnit(BigDecimal hedgeUnit) {
        this.hedgeUnit = hedgeUnit;
    }

    public BigDecimal getTradeAmountRatio() {
        return tradeAmountRatio;
    }

    public void setTradeAmountRatio(BigDecimal tradeAmountRatio) {
        this.tradeAmountRatio = tradeAmountRatio;
    }

    public BigDecimal getTradePriceSigmaRatio() {
        return tradePriceSigmaRatio;
    }

    public void setTradePriceSigmaRatio(BigDecimal tradePriceSigmaRatio) {
        this.tradePriceSigmaRatio = tradePriceSigmaRatio;
    }

    public BigDecimal getAmountSigmaRatio() {
        return amountSigmaRatio;
    }

    public void setAmountSigmaRatio(BigDecimal amountSigmaRatio) {
        this.amountSigmaRatio = amountSigmaRatio;
    }

    public BigDecimal getPriceSigmaRatio() {
        return priceSigmaRatio;
    }

    public void setPriceSigmaRatio(BigDecimal priceSigmaRatio) {
        this.priceSigmaRatio = priceSigmaRatio;
    }
}
