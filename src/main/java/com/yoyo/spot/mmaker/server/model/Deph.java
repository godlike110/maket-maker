package com.yoyo.spot.mmaker.server.model;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * 深度
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/18
 **/

public class Deph {

    List<BigDecimal> bidPrices = Lists.newArrayList();

    List<BigDecimal> askPrices = Lists.newArrayList();

    List<BigDecimal> bidSizes = Lists.newArrayList();

    List<BigDecimal> askSizes = Lists.newArrayList();


    public List<BigDecimal> getBidPrices() {
        return bidPrices;
    }

    public void setBidPrices(List<BigDecimal> bidPrices) {
        this.bidPrices = bidPrices;
    }

    public List<BigDecimal> getAskPrices() {
        return askPrices;
    }

    public void setAskPrices(List<BigDecimal> askPrices) {
        this.askPrices = askPrices;
    }

    public List<BigDecimal> getBidSizes() {
        return bidSizes;
    }

    public void setBidSizes(List<BigDecimal> bidSizes) {
        this.bidSizes = bidSizes;
    }

    public List<BigDecimal> getAskSizes() {
        return askSizes;
    }

    public void setAskSizes(List<BigDecimal> askSizes) {
        this.askSizes = askSizes;
    }
}
