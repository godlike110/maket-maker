package com.yoyo.spot.mmaker.server.enums;

import java.util.concurrent.Exchanger;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/21
 **/

public enum ExChange {

    HUOBI("huobi"),
    OKEX("okex"),
    BINANCE("binance");

    private String name;

    ExChange(String name) {
        this.name=name;
    }

    public static ExChange getByName(String name) {
        for(ExChange exChange: ExChange.values()) {
            if(exChange.name.equalsIgnoreCase(name)) {
                return exChange;
            }
        }
        return ExChange.HUOBI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
