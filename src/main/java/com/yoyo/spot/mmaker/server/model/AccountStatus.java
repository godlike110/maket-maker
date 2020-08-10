package com.yoyo.spot.mmaker.server.model;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoyo.spot.openapi.client.model.AccountBalance;
import com.yoyo.spot.openapi.client.model.OrderInfo;

import java.util.List;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/18
 **/
public class AccountStatus {

    List<OrderInfo> openOrders = Lists.newArrayList();
    List<AccountBalance> assets = Lists.newArrayList();

    public List<OrderInfo> getOpenOrders() {
        return openOrders;
    }

    public void setOpenOrders(List<OrderInfo> openOrders) {
        this.openOrders = openOrders;
    }

    public List<AccountBalance> getAssets() {
        return assets;
    }

    public void setAssets(List<AccountBalance> assets) {
        this.assets = assets;
    }
}
