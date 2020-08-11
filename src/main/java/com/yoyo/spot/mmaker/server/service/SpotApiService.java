package com.yoyo.spot.mmaker.server.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yoyo.spot.openapi.client.YoSpotApiRestClient;
import com.yoyo.spot.openapi.client.enums.OrderSide;
import com.yoyo.spot.openapi.client.model.AccountBalance;
import com.yoyo.spot.openapi.client.model.BookEntity;
import com.yoyo.spot.openapi.client.model.OrderBook;
import com.yoyo.spot.openapi.client.model.OrderInfo;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/13
 **/
@Service
public class SpotApiService {

    private static Logger logger = LoggerFactory.getLogger(SpotApiService.class);

    @Resource(name = "spotApiClient")
    private YoSpotApiRestClient apiRestClient;

    /**
     * 获取资产信息
     * @return
     */
    public Map<String, AccountBalance> getAccounts() {

        AccountBalance[] accountBalances = apiRestClient.getAllUserAssets("");

        Map<String,AccountBalance> accountMap = Maps.newHashMap();

        for(int i=0;i<accountBalances.length;i++) {
            accountMap.put(accountBalances[i].getAssetSymbol(),accountBalances[i]);
        }
        return accountMap;

    }

    /**
     * 撤单
     * @param orderIds
     */
    public void cancalOrders(String symbol,Long... orderIds) {

        Long[] result = apiRestClient.cancelOrders(symbol,Arrays.asList(orderIds));
        logger.info("cancelOrder result:{}", JSON.toJSONString(result));

    }

    /**
     * 普通下单
     * @param symbol
     * @param side
     * @param price
     * @param qty
     * @return
     */
    public Long placeOrder(String symbol, OrderSide side, BigDecimal price,BigDecimal qty) {

        Long orderId = apiRestClient.placeOrder(symbol,side,price,qty);

        return orderId;

    }

    /**
     * 特殊单
     * @param symbol
     * @param side
     * @param price
     * @param qty
     * @return
     */
    public Long privateOrder(String symbol, OrderSide side, BigDecimal price,BigDecimal qty) {

        Long orderId = apiRestClient.privatePlaceOrder(symbol,side,price,qty);

        return orderId;

    }

    /**
     * 挂单表
     * @param symbol
     * @param limit
     * @return
     */
    public OrderBook getOrderBook(String symbol,int limit) {
        return apiRestClient.getOrderBook(symbol,limit);
    }

    /**
     * 获取用户挂单列表
     * @param symbol
     * @param pageNo
     * @param pageSize
     * @return
     */
    public OrderBook getOpenOrder(String symbol, int pageNo, int pageSize) {

        List<OrderInfo> userOrderList = apiRestClient.getOpenOrder(symbol,pageNo,pageSize);

        List<BookEntity> buyList = Lists.newArrayList();
        List<BookEntity> sellList = Lists.newArrayList();


        for (int i = 0;i<userOrderList.size();i++) {

            OrderInfo orderInfo = JSON.parseObject(JSON.toJSONString(userOrderList.get(i)),OrderInfo.class);

            if(orderInfo.getSide().equalsIgnoreCase(OrderSide.BUY.getType())) {
                buyList.add(new BookEntity(new BigDecimal(orderInfo.getPrice()),new BigDecimal(orderInfo.getOrigQty())).setOrderId(Long.parseLong(orderInfo.getOrderId())));
            }

            if(orderInfo.getSide().equalsIgnoreCase(OrderSide.SELL.getType())) {
                sellList.add(new BookEntity(new BigDecimal(orderInfo.getPrice()),new BigDecimal(orderInfo.getOrigQty())).setOrderId(Long.parseLong(orderInfo.getOrderId())));
            }
        }

        sellList.sort(new Comparator<BookEntity>() {
            @Override
            public int compare(BookEntity o1, BookEntity o2) {
                if(o1.getPrice().compareTo(o2.getPrice())>=0) {
                    return 1;
                }
                return -1;
            }
        });

        buyList.sort(new Comparator<BookEntity>() {
            @Override
            public int compare(BookEntity o1, BookEntity o2) {
                if(o1.getPrice().compareTo(o2.getPrice())>=0) {
                    return -1;
                }
                return 1;
            }
        });

        return new OrderBook(buyList,sellList);

    }

    /**
     * 获取最新成交价格
     * @param symbol
     * @return
     */
    public BigDecimal getLastPrice(String symbol) {
        return apiRestClient.getLastPrice(symbol);
    }

    /**
     * 取消所有订单
     * @param symbol
     */
    public void cancelAll(String symbol) {

        apiRestClient.cancelAll(symbol);

        return;

    }

}
