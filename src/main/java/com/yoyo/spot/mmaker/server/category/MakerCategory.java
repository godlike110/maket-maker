package com.yoyo.spot.mmaker.server.category;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.yoyo.spot.mmaker.server.service.SpotApiService;
import com.yoyo.spot.openapi.client.enums.OrderSide;
import com.yoyo.spot.openapi.client.model.BookEntity;
import com.yoyo.spot.openapi.client.model.OrderBook;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

/**
 * GGC/usdt 挂单逻辑
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/13
 **/
@Service
public class MakerCategory {

    private static Logger logger = LoggerFactory.getLogger(MakerCategory.class);

    @Autowired
    private SpotApiService spotApiService;


    String symbol = "BWUSDT";
    //最小挂单深度
    private int minDepth = 13;
    //挂单最小跨度
    private BigDecimal step = new BigDecimal("0.0007");
    private BigDecimal minStep = new BigDecimal("0.0001");
    private BigDecimal qty = new BigDecimal(10);



    @Scheduled(fixedRate = 3000)
    public void makerRun() throws InterruptedException {

        OrderBook orderBook = spotApiService.getOrderBook(symbol,50);

        OrderBook myOrderBook = spotApiService.getOpenOrder(symbol,1,minDepth*2);

        BigDecimal lastPrice = spotApiService.getLastPrice(symbol);
        BigDecimal sellOnePrice = getFirstPrice(orderBook.getSellList());
        BigDecimal buyOnePrice = getFirstPrice(orderBook.getBuyList());

        int emptyStep = sellOnePrice.subtract(buyOnePrice).divide(minStep).intValue()-1;

        if(emptyStep>3) {

            Random random = new Random();
            int num = random.nextInt(emptyStep)+1;
            BigDecimal price = buyOnePrice.add(minStep.multiply(new BigDecimal(num)));
            Long flushOrder = spotApiService.privateOrder(symbol,new Random().nextInt(2)==0?OrderSide.BUY:OrderSide.SELL,price,new BigDecimal(new Random().nextInt(50)+10));
            logger.info("flush price:{}",price.toPlainString());
        }


        if((myOrderBook.getBuyList().size()==0)) {
            //全部填充买单
            fillOrder(symbol,OrderSide.BUY,lastPrice.subtract(step),minDepth);
        }

        if((myOrderBook.getSellList().size()==0)) {
            //全部填充卖单
            fillOrder(symbol,OrderSide.SELL,lastPrice.add(step),minDepth);
        }

        //卖单
        if(sellOnePrice.compareTo(new BigDecimal(0))>0  && myOrderBook.getSellList().size()>0) {

            BigDecimal sellOnePriceOffset = myOrderBook.getSellList().get(0).getPrice().subtract(sellOnePrice);

            if(sellOnePriceOffset.compareTo(new BigDecimal(0)) >= 0) {

                int offsetStep = sellOnePriceOffset.divide(step, 2,RoundingMode.DOWN).intValue();
                int sellSize = myOrderBook.getSellList().size();

                int needFill = minDepth - sellSize;

                if(offsetStep==0) {
                    if(needFill>0) {
                        fillOrder(symbol,OrderSide.SELL,myOrderBook.getSellList().get(myOrderBook.getSellList().size()-1).getPrice().add(step),needFill);
                    }
                } else if (offsetStep>minDepth || offsetStep>=myOrderBook.getSellList().size()) {
                    cancelOrder(myOrderBook,OrderSide.SELL);
                    fillOrder(symbol,OrderSide.SELL,sellOnePrice,minDepth);
                } else if (offsetStep<myOrderBook.getSellList().size()) {
                    if(offsetStep==needFill) {
                        fillOrder(symbol,OrderSide.SELL,sellOnePrice,needFill);
                    } else if(offsetStep>needFill && needFill==0) {
                        fillOrder(symbol,OrderSide.SELL,sellOnePrice,offsetStep);
                        cancelLimitOrder(myOrderBook,OrderSide.SELL,offsetStep);
                    } else if(offsetStep>needFill && needFill>0) {
                        fillOrder(symbol,OrderSide.SELL,sellOnePrice,needFill);
                        cancelOrder(myOrderBook,OrderSide.SELL);
                        fillOrder(symbol,OrderSide.SELL,sellOnePrice.add(step.multiply(new BigDecimal(needFill+1))),minDepth-needFill);
                    } else if(offsetStep<needFill) {
                        fillOrder(symbol,OrderSide.SELL,sellOnePrice,offsetStep);
                        fillOrder(symbol,OrderSide.SELL,myOrderBook.getSellList().get(myOrderBook.getSellList().size()).getPrice().add(step),minDepth-myOrderBook.getSellList().size()-offsetStep);
                    }
                }


            }

        }


        //买单
        if(buyOnePrice.compareTo(new BigDecimal(0))>0  && myOrderBook.getBuyList().size()>0) {

            BigDecimal buyOnePriceOffset = myOrderBook.getBuyList().get(0).getPrice().subtract(buyOnePrice).negate();

            if(buyOnePriceOffset.compareTo(new BigDecimal(0)) >= 0) {

                int offsetStep = buyOnePriceOffset.divide(step,2,RoundingMode.DOWN).intValue();
                int buySize = myOrderBook.getBuyList().size();

                int needFill = minDepth - buySize;

                if(offsetStep==0) {
                    if(needFill>0) {
                        fillOrder(symbol,OrderSide.BUY,myOrderBook.getBuyList().get(myOrderBook.getBuyList().size()-1).getPrice().subtract(step),needFill);
                    }
                } else if (offsetStep>minDepth || offsetStep>=myOrderBook.getBuyList().size()) {
                    cancelOrder(myOrderBook,OrderSide.BUY);
                    fillOrder(symbol,OrderSide.BUY,buyOnePrice,minDepth);
                } else if (offsetStep<myOrderBook.getBuyList().size()) {
                    if(offsetStep==needFill) {
                        fillOrder(symbol,OrderSide.BUY,buyOnePrice,needFill);
                    } else if(offsetStep>needFill && needFill>0) {
                        fillOrder(symbol,OrderSide.BUY,buyOnePrice,needFill);
                        cancelOrder(myOrderBook,OrderSide.BUY);
                        fillOrder(symbol,OrderSide.BUY,buyOnePrice.subtract(step.multiply(new BigDecimal(needFill+1))),minDepth-needFill);
                    } else if(offsetStep>needFill && needFill==0) {
                        fillOrder(symbol,OrderSide.BUY,buyOnePrice,offsetStep);
                        cancelLimitOrder(myOrderBook,OrderSide.BUY,offsetStep);
                    } else if(offsetStep<needFill) {
                        fillOrder(symbol,OrderSide.BUY,buyOnePrice,offsetStep);
                        fillOrder(symbol,OrderSide.BUY,myOrderBook.getBuyList().get(myOrderBook.getBuyList().size()).getPrice().subtract(step),minDepth-myOrderBook.getBuyList().size()-offsetStep);
                    }
                }


            }

            BigDecimal selladd = new BigDecimal(new Random().nextInt(500)+33);
            BigDecimal buyadd = new BigDecimal(new Random().nextInt(500)+55);

            long buyOrerId = spotApiService.placeOrder(symbol,OrderSide.BUY,buyOnePrice,buyadd);
            Thread.sleep(400);
            spotApiService.cancalOrders(symbol,buyOrerId);
            long sellOrderId = spotApiService.placeOrder(symbol,OrderSide.SELL,sellOnePrice,selladd);
            Thread.sleep(400);
            spotApiService.cancalOrders(symbol,sellOrderId);


        }

    }

    public void cancelLimitOrder(OrderBook orderBook,OrderSide side,int limt) {
        List<BookEntity> cancelList = side==OrderSide.BUY?orderBook.getBuyList():orderBook.getSellList();
        List<Long> orders = Lists.newArrayList();
        for(int i=1;i<=limt;i++) {
            orders.add(cancelList.get(cancelList.size()-i).getOrderId());
        }
        spotApiService.cancalOrders(symbol,orders.toArray(new Long[orders.size()]));

    }

    public void cancelOrder(OrderBook orderBook,OrderSide side) {

        switch (side) {
            case SELL:
                List<Long> sellOrders = Lists.newArrayList(Collections2.transform(orderBook.getSellList(), new Function<BookEntity, Long>() {
                    @Nullable
                    @Override
                    public Long apply(@Nullable BookEntity bookEntity) {
                        return bookEntity.getOrderId();
                    }
                }));
                spotApiService.cancalOrders(symbol,sellOrders.toArray(new Long[sellOrders.size()]));
                break;
            case BUY:
                List<Long> buyOrders = Lists.newArrayList(Collections2.transform(orderBook.getBuyList(), new Function<BookEntity, Long>() {
                    @Nullable
                    @Override
                    public Long apply(@Nullable BookEntity bookEntity) {
                        return bookEntity.getOrderId();
                    }
                }));
                spotApiService.cancalOrders(symbol,buyOrders.toArray(new Long[buyOrders.size()]));
                break;
        }


    }



    public BigDecimal getFirstPrice(List<BookEntity> bookEntities) {

        if(bookEntities.size()>0) {
            return bookEntities.get(0).getPrice();
        } else {
            return new BigDecimal(0);
        }

    }

    /**
     * 填单
     * @param symbol
     * @param orderSide
     * @param startPrice
     * @param nums
     */
    public void fillOrder(String symbol,OrderSide orderSide,BigDecimal startPrice,int nums) {

        for(int i=0;i<nums;i++) {
            spotApiService.placeOrder(symbol,orderSide,startPrice.add(orderSide==OrderSide.BUY?step.negate().multiply(new BigDecimal(i)):step.multiply(new BigDecimal(i))),new BigDecimal(new Random().nextInt(i*20+1)+111));
        }


    }

}
