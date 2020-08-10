package com.yoyo.spot.mmaker.server.enums;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/21
 **/

public enum TradeDirection {

    BUY("buy"),
    SELL("sell");

    private String code;

    TradeDirection(String code) {
        this.code = code;
    }



}
