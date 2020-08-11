package com.yoyo.spot.mmaker.server.runners;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.huobi.client.model.enums.TradeDirection;
import com.yoyo.spot.mmaker.server.config.CategoryConfig;
import com.yoyo.spot.mmaker.server.model.DataEngine;
import com.yoyo.spot.mmaker.server.model.Deph;
import com.yoyo.spot.mmaker.server.model.Trade;
import com.yoyo.spot.mmaker.server.model.TradeEvent;
import com.yoyo.spot.mmaker.server.util.MathUtil;
import com.yoyo.spot.openapi.client.YoSpotApiRestClient;
import com.yoyo.spot.openapi.client.enums.OrderSide;
import com.yoyo.spot.openapi.client.model.AccountBalance;
import com.yoyo.spot.openapi.client.model.OrderInfo;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/19
 **/

public class MarketMaker implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(MarketMaker.class);

    private static long floodTime = 0l;

    private String symbol;
    private YoSpotApiRestClient apiRestClient;
    private Object initialPosition;
    private String assetSymbol;
    private String mmSymbol;
    private BigDecimal initialSymbolAsset;
    private DataEngine gData;
    private CategoryConfig symbolConfig;

    public MarketMaker(YoSpotApiRestClient apiRestClient,DataEngine gData) {
        this.symbol = gData.getSymbol();
        this.apiRestClient = apiRestClient;
        this.initialPosition = gData;
        this.assetSymbol = gData.getConfig().getAssetSymbol();
        this.mmSymbol = gData.getConfig().getMmSymbol();
        this.gData = gData;
        this.initialSymbolAsset = initAsset();
        this.symbolConfig = gData.getConfig();
    }

    public BigDecimal initAsset() {
        AccountBalance[] accountBalances = apiRestClient.getAllUserAssets(assetSymbol);
        for (AccountBalance ab:accountBalances) {
            if(ab.getAssetSymbol().equalsIgnoreCase(this.assetSymbol)) {
                return ab.getTotalBalance();
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal symbolAsset() {
        List<AccountBalance> accountBalances =  gData.getStatus().getAssets();
        for (AccountBalance ab:accountBalances) {
            if(ab.getAssetSymbol().equalsIgnoreCase(this.assetSymbol)) {
                return ab.getTotalBalance();
            }
        }
        return BigDecimal.ZERO;
    }

    private void mm() {

        if(gData.getDepthUpdated()==null) {
            return;
        }

        gData.setDepthUpdated(false);

        Deph deph = gData.getDeph();
        if(deph.getAskPrices().size()==0) {
            return;
        }

        BigDecimal hedgeFee = gData.getConfig().getHedgeFee();
        BigDecimal mmDistance = gData.getConfig().getMmDistance();
        BigDecimal maxDistance = gData.getConfig().getMaxDistance();
        BigDecimal minDistance = gData.getConfig().getMinDistance();

        BigDecimal maxPosition = gData.getConfig().getMaxPosition();

        BigDecimal symbolAsset = symbolAsset();

        BigDecimal posRate = symbolAsset.subtract(initialSymbolAsset).divide(maxPosition,18, RoundingMode.HALF_UP);
        BigDecimal positionDistance = maxDistance.subtract(mmDistance).multiply(posRate);

        BigDecimal bidDistance = mmDistance.add(positionDistance);
        BigDecimal tempBidDistance = bidDistance.compareTo(minDistance)>0?bidDistance:minDistance;
        bidDistance = tempBidDistance.compareTo(maxDistance)>0?maxDistance:tempBidDistance;

        BigDecimal askDistance = mmDistance.subtract(positionDistance);
        BigDecimal tempAskdistance = askDistance.compareTo(minDistance)>0?askDistance:minDistance;
        askDistance = tempAskdistance.compareTo(maxDistance)>=0?maxDistance:tempAskdistance;

        BigDecimal midPrice = deph.getAskPrices().get(0).add(deph.getBidPrices().get(0)).divide(new BigDecimal(2));

        mmOrder(midPrice,bidDistance,askDistance);



    }

    public BigDecimal getPrice(BigDecimal price,BigDecimal pricision) {

        return new BigDecimal(price.divide(pricision,2).intValue()).multiply(pricision);

    }

    public void flood() {

        if(gData.getTradeUpdated()==null) {
            return;
        }

        if(System.currentTimeMillis()/1000-floodTime<1) {
            return;
        }

        floodTime = System.currentTimeMillis()/1000;

        TradeEvent tradeEvent =  gData.getTradeEvent();

        if(!tradeEvent.getSymbol().equalsIgnoreCase(this.symbol)) {
           return;
        }

        BigDecimal amount = BigDecimal.ZERO;
        int buyCount = 1;
        int sellCount = 1;
        BigDecimal lastPrice = BigDecimal.ZERO;

        for(Trade trade:tradeEvent.getTradeList()) {

            amount = amount.add(trade.getAmount().multiply(gData.getConfig().getPriceRate()).setScale(gData.getConfig().getAmountFormatter(),RoundingMode.HALF_UP));

            String direction = trade.getDirection().toString();
            if(direction.equalsIgnoreCase(TradeDirection.SELL.toString())) {
                sellCount = sellCount + 1;
            } else {
                buyCount = buyCount + 1;
            }

            double buyProd = buyCount / (buyCount + sellCount);

            com.yoyo.spot.openapi.client.enums.OrderSide side = null;

            Double gau = MathUtil.normal(0l,1l);

            if(gau.compareTo(buyProd)<=0) {
                side = com.yoyo.spot.openapi.client.enums.OrderSide.BUY;
            } else {
                side = OrderSide.SELL;
            }

            amount = amount.multiply(symbolConfig.getTradeAmountRatio());

            BigDecimal amountSigmaRatio = symbolConfig.getAmountSigmaRatio();
            amount = new BigDecimal(Math.max(amount.divide(new BigDecimal(10),symbolConfig.getAmountFormatter(),RoundingMode.HALF_UP).doubleValue(),amount.multiply(amountSigmaRatio).setScale(symbolConfig.getAmountFormatter(),RoundingMode.HALF_UP).doubleValue()));

            Deph deph = gData.getDeph();

            if(deph.getAskPrices().size()==0) {
                return;
            }

            BigDecimal bestBid = deph.getBidPrices().get(0);
            BigDecimal bestAsk = deph.getAskPrices().get(0);

            BigDecimal midPrice = bestAsk.add(bestBid).divide(new BigDecimal(2),symbolConfig.getPriceFormatter()+1);

            BigDecimal price = new BigDecimal(MathUtil.normal(midPrice.floatValue(),midPrice.multiply(symbolConfig.getTradePriceSigmaRatio()).floatValue())).setScale(gData.getConfig().getPriceFormatter(),RoundingMode.HALF_UP);

            if(amount.compareTo(gData.getConfig().getMinAmount())<0) {
                amount = gData.getConfig().getMinAmount();
            }

            apiRestClient.privatePlaceOrder(mmSymbol,side,price,amount);

        }

    }

    public void cancelOrders(List<OrderInfo> orders) {

        if(null==orders || orders.size()==0) {
            return;
        }

        int maxCancelNum = 45;
        if(orders.size()>maxCancelNum) {
            orders = orders.subList(0,maxCancelNum);
        }

        List<Long> orderIds = Lists.newArrayList(Collections2.transform(orders, new Function<OrderInfo, Long>() {
            @Nullable
            @Override
            public Long apply(@Nullable OrderInfo orderInfo) {
                Long id = Long.parseLong(orderInfo.getOrderId());
               // apiRestClient.cancelOrders(Lists.newArrayList(id));
                return id;
            }
        }));

        apiRestClient.cancelOrders(symbol,orderIds);

        return;

    }

    public void newOrder(BigDecimal price,BigDecimal amount,OrderSide side) {

        price = new BigDecimal(MathUtil.normal(price.floatValue(),price.multiply(gData.getConfig().getLevelDistance()).multiply(gData.getConfig().getPriceSigmaRatio()).floatValue())).setScale(gData.getConfig().getPriceFormatter(),RoundingMode.HALF_UP);

        BigDecimal newAmount = new BigDecimal(Math.max(amount.divide(new BigDecimal(10),gData.getConfig().getAmountFormatter()).doubleValue(),MathUtil.normal(amount.floatValue(),amount.multiply(gData.getConfig().getAmountSigmaRatio()).floatValue()))).setScale(gData.getConfig().getAmountFormatter(),RoundingMode.HALF_UP);

        if(newAmount.compareTo(gData.getConfig().getMinAmount())<0) {
            newAmount = gData.getConfig().getMinAmount();
        }

        apiRestClient.placeOrder(mmSymbol,side,price,newAmount);

    }

    public void placeMMOrders(BigDecimal bidPrice,BigDecimal askPrice,
                              BigDecimal minBidPrice,BigDecimal maxAskPrice,
                              int nowBids,int nowAsks) {

        int mmLevels = gData.getConfig().getMmLevels();
        BigDecimal amount = gData.getConfig().getAmount();
        BigDecimal levelDistance = gData.getConfig().getLevelDistance();
        newOrder(bidPrice,amount,OrderSide.BUY);
        newOrder(askPrice,amount,OrderSide.SELL);

        for(int i=nowBids;i<mmLevels;i++) {
            BigDecimal priOffset = new BigDecimal(1).subtract(levelDistance);
            minBidPrice = minBidPrice.multiply(priOffset);
            newOrder(minBidPrice,amount,OrderSide.BUY);
        }

        for(int i=nowAsks;i<mmLevels;i++) {
            BigDecimal priOffset = new BigDecimal(1).add(levelDistance);
            maxAskPrice = maxAskPrice.multiply(priOffset);
            newOrder(maxAskPrice,amount,OrderSide.SELL);
        }

    }


    public void mmOrder(BigDecimal midPrice,BigDecimal bidDistance,BigDecimal askDistance) {

        BigDecimal bidOffsetRate = new BigDecimal(1).subtract(bidDistance);
        BigDecimal bidPrice = midPrice.multiply(bidOffsetRate);

        BigDecimal askOffsetRate = new BigDecimal(1).add(askDistance);
        BigDecimal askPrice = midPrice.multiply(askOffsetRate);

        List<OrderInfo> openOrders = gData.getStatus().getOpenOrders();

        List<OrderInfo> bidOrders = Lists.newArrayList();
        List<OrderInfo> askOrders = Lists.newArrayList();
        List<OrderInfo> cancelOrders = Lists.newArrayList();
        Set<String> priceSet = Sets.newHashSet();


        BigDecimal levelDistance = gData.getConfig().getLevelDistance();

        BigDecimal minBidPrice = bidPrice;
        BigDecimal masAskPrice = askPrice;

        for(int i=0;i<openOrders.size();i++) {

            OrderInfo orderInfo = JSON.parseObject(JSON.toJSONString(openOrders.get(i)),OrderInfo.class);

            if(!orderInfo.getSymbol().equalsIgnoreCase(mmSymbol)) {
                continue;
            }

            if(priceSet.contains(orderInfo.getPrice())) {
                cancelOrders.add(orderInfo);
                continue;
            } else {
                priceSet.add(orderInfo.getPrice());
            }

            BigDecimal price = new BigDecimal(orderInfo.getPrice());
            String side = orderInfo.getSide();

            if(side.equalsIgnoreCase(OrderSide.BUY.getType())) {

                if(price.compareTo(bidPrice) >0 ||
                   price.divide(bidPrice,18,RoundingMode.HALF_UP).subtract(new BigDecimal(1)).abs().compareTo(levelDistance)<0) {

                    cancelOrders.add(orderInfo);

                } else {

                    bidOrders.add(orderInfo);
                    minBidPrice = price;

                }


            } else {

                if(price.compareTo(askPrice)<0 ||
                        price.divide(askPrice,18,RoundingMode.HALF_UP).subtract(new BigDecimal(1)).abs().compareTo(levelDistance)<0){

                    cancelOrders.add(orderInfo);

                } else {

                    askOrders.add(orderInfo);
                    masAskPrice = price;

                }

            }


        }

        int mmLevel = gData.getConfig().getMmLevels();

        if(bidOrders.size()>=mmLevel) {
            bidOrders.sort(new Comparator<OrderInfo>() {
                @Override
                public int compare(OrderInfo o1, OrderInfo o2) {
                    int result = new BigDecimal(o1.getPrice()).compareTo(new BigDecimal(o2.getPrice()));
                    if(result>0) {
                        return -1;
                    } else if(result<0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            for(int i=mmLevel-1;i<bidOrders.size();i++) {
                cancelOrders.add(bidOrders.get(i));
            }
        }

        if(askOrders.size()>=mmLevel) {
            askOrders.sort(new Comparator<OrderInfo>() {
                @Override
                public int compare(OrderInfo o1, OrderInfo o2) {
                    int result = new BigDecimal(o1.getPrice()).compareTo(new BigDecimal(o2.getPrice()));
                    if(result>0) {
                        return 1;
                    } else if(result<0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            for(int i=mmLevel-1;i<askOrders.size();i++) {
                cancelOrders.add(askOrders.get(i));
            }
        }

        cancelOrders(cancelOrders);
        placeMMOrders(bidPrice,askPrice,minBidPrice,masAskPrice,bidOrders.size()+1,askOrders.size()+1);


    }


    @Override
    public void run() {

        logger.info("交易对:{} 挂单刷量策略启动",gData.getConfig().getMmSymbol());

        while (true) {
            try {
                Thread.sleep(gData.getConfig().getSensitivity());
                if(gData.getConfig().isDown()) {
                    logger.info("交易对:{} 挂单刷量策略关闭",gData.getConfig().getMmSymbol());
                    break;
                }
                work();
            } catch (Exception e) {
                logger.error("market maker error",e);
            }
        }

    }

    private void work() {
        flood();
        mm();
    }
}
