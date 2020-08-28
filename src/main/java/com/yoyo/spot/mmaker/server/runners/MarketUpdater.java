package com.yoyo.spot.mmaker.server.runners;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.huobi.client.SubscriptionClient;
import com.huobi.client.model.PriceDepth;
import com.huobi.client.model.event.PriceDepthEvent;
import com.huobi.client.model.event.TradeEvent;
//import com.okcoin.commons.okex.open.api.bean.spot.result.Book;
//import com.okcoin.commons.okex.open.api.bean.spot.result.Trade;
//import com.okcoin.commons.okex.open.api.config.WebSocketConfig;
//import com.okcoin.commons.okex.open.api.websocket.WebSocketClient;
import com.yoyo.spot.mmaker.server.enums.ExChange;
import com.yoyo.spot.mmaker.server.enums.TradeDirection;
import com.yoyo.spot.mmaker.server.model.DataEngine;
import com.yoyo.spot.mmaker.server.model.Deph;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/21
 **/

public class MarketUpdater implements Runnable {

    private DataEngine gData;

    private String exchange;


    public MarketUpdater(String exchange,DataEngine gData) {
        this.gData =gData;
        this.exchange = exchange;
    }

//    private void processOkData(Object data) {
//        if(data instanceof Book) {
//            Book okBook = (Book) data;
//            Deph deph = new Deph();
//            for(int i=0;i<5;i++) {
//                deph.getBidPrices().add(new BigDecimal(okBook.getBids().get(i)[0]).divide(gData.getConfig().getPriceRate(),gData.getConfig().getPriceFormatter()+1, RoundingMode.HALF_UP));
//                deph.getAskPrices().add(new BigDecimal(okBook.getAsks().get(i)[0]).divide(gData.getConfig().getPriceRate(),gData.getConfig().getPriceFormatter()+1, RoundingMode.HALF_UP));
//                deph.getBidSizes().add(new BigDecimal(okBook.getBids().get(i)[1]).multiply(gData.getConfig().getPriceRate()).setScale(gData.getConfig().getAmountFormatter()+1,RoundingMode.HALF_UP));
//                deph.getAskSizes().add(new BigDecimal(okBook.getAsks().get(i)[1]).multiply(gData.getConfig().getPriceRate()).setScale(gData.getConfig().getAmountFormatter()+1,RoundingMode.HALF_UP));
//            }
//            gData.setDeph(deph);
//            gData.setDepthUpdated(true);
//            gData.setLastDepthTime(System.currentTimeMillis());
//        } else if (data instanceof List) {
//            List<com.okcoin.commons.okex.open.api.bean.spot.result.Trade> okList = (List<Trade>) data;
//            com.yoyo.spot.mmaker.server.model.TradeEvent tradeEvent = new com.yoyo.spot.mmaker.server.model.TradeEvent();
//            tradeEvent.setSymbol(gData.getSymbol());
//            tradeEvent.setTimestamp(System.currentTimeMillis());
//            Collection nList = Collections2.transform(okList, new Function<Trade, com.yoyo.spot.mmaker.server.model.Trade>() {
//                @Override
//                public com.yoyo.spot.mmaker.server.model.Trade apply(Trade trade) {
//                    com.yoyo.spot.mmaker.server.model.Trade nt = new com.yoyo.spot.mmaker.server.model.Trade();
//                    nt.setAmount(new BigDecimal(trade.getSize()));
//                    nt.setDirection(trade.getSide().equalsIgnoreCase("buy")? TradeDirection.BUY:TradeDirection.SELL);
//                    nt.setPrice(new BigDecimal(trade.getPrice()));
//                    return nt;
//                }
//            });
//            tradeEvent.setTradeList(Lists.newArrayList(nList));
//            gData.setTradeEvent(tradeEvent);
//            gData.setTradeUpdated(true);
//            gData.setLastTradeTime(System.currentTimeMillis());
//        }
//
//    }

    private Deph makeDepth(PriceDepthEvent event, DataEngine gData) {
        Deph deph = new Deph();
        PriceDepth priceDepth = event.getData();
        for(int i=0;i<5;i++) {
            deph.getBidPrices().add(priceDepth.getBids().get(i).getPrice().divide(gData.getConfig().getPriceRate(),gData.getConfig().getPriceFormatter()+1, RoundingMode.HALF_UP));
            deph.getAskPrices().add(priceDepth.getAsks().get(i).getPrice().divide(gData.getConfig().getPriceRate(),gData.getConfig().getPriceFormatter()+1, RoundingMode.HALF_UP));
            deph.getBidSizes().add(priceDepth.getBids().get(i).getAmount().multiply(gData.getConfig().getPriceRate()).setScale(gData.getConfig().getAmountFormatter()+1,RoundingMode.HALF_UP));
            deph.getAskSizes().add(priceDepth.getAsks().get(i).getAmount().multiply(gData.getConfig().getPriceRate()).setScale(gData.getConfig().getAmountFormatter()+1,RoundingMode.HALF_UP));
        }
        return deph;
    }

    private void depthCallBack(PriceDepthEvent event,DataEngine gData) {

        Deph deph = makeDepth(event,gData);
        gData.setDeph(deph);
        gData.setDepthUpdated(true);
        gData.setLastDepthTime(System.currentTimeMillis());

    }

    private void tradeCallBack(TradeEvent event, DataEngine gData) {

        gData.setTradeEvent(makeNewEvent(event));
        gData.setTradeUpdated(true);
        gData.setLastTradeTime(System.currentTimeMillis());

    }

    public com.yoyo.spot.mmaker.server.model.TradeEvent makeNewEvent(TradeEvent tradeEvent) {

        com.yoyo.spot.mmaker.server.model.TradeEvent newExent = JSON.parseObject(JSON.toJSONString(tradeEvent), com.yoyo.spot.mmaker.server.model.TradeEvent.class);
        return newExent;

    }


    @Override
    public void run() {
        ExChange ex = ExChange.getByName(exchange);

        switch (ex) {
            case HUOBI:
                SubscriptionClient subscriptionClient = SubscriptionClient.create();
                subscriptionClient.subscribePriceDepthEvent(gData.getSymbol().toLowerCase(), (priceDepthEvent) -> {
                    depthCallBack(priceDepthEvent,gData);
                });

                subscriptionClient.subscribeTradeEvent(gData.getSymbol().toLowerCase(), (tradeEvent) -> {
                    tradeCallBack(tradeEvent,gData);
                });
                break;
            case OKEX:
//                WebSocketClient webSocketClient = new WebSocketClient();
//                WebSocketConfig.publicConnect(webSocketClient);
//                ArrayList<String> commod = new ArrayList<>();
//                commod.add("spot/trade:" + gData.getSymbol().toUpperCase());
//                commod.add("spot/depth5:" + gData.getSymbol().toUpperCase());
//                webSocketClient.subscribe(commod,data -> {
//                    processOkData(data);
//                });
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                break;
            case BINANCE:
                break;
            default:
                break;


        }
    }
}
