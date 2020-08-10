package com.yoyo.spot.mmaker.server.runners;

import com.yoyo.spot.mmaker.server.model.DataEngine;
import com.yoyo.spot.openapi.client.YoSpotApiRestClient;
import com.yoyo.spot.openapi.client.model.AccountBalance;
import com.yoyo.spot.openapi.client.model.OrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/18
 **/

public class StatusUpdater implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(StatusUpdater.class);

    private YoSpotApiRestClient apiRestClient;
    private DataEngine gData;

    public StatusUpdater(YoSpotApiRestClient apiRestClient, DataEngine gData) {
        this.apiRestClient = apiRestClient;
        this.gData = gData;
    }

    @Override
    public void run() {

        logger.info("交易对:{} 账户订单同步启动",gData.getConfig().getMmSymbol());

        while (true) {
            try {
                Thread.sleep(200);

                if(gData.getConfig().isDown()) {
                    logger.info("交易对:{} 账户订单同步关闭",gData.getConfig().getMmSymbol());
                    break;
                }

               // upPriceRate();
                List<AccountBalance> accountBalances = getAssets();
                List<OrderInfo> orderInfos = getOpenOrder();
                gData.getStatus().setAssets(accountBalances);
                gData.getStatus().setOpenOrders(orderInfos);

            } catch (Exception e) {
                logger.error("StatusUpdater error",e);
            }


        }

    }

    public void upPriceRate() {

        if(gData.getConfig().isSingle() && gData.getTradeUpdated()) {
            //单机币更新价格比例

            BigDecimal lastPrice = apiRestClient.getLastPrice(gData.getConfig().getMmSymbol());

            BigDecimal thirdPrice = gData.getDeph().getAskPrices().get(0);

            gData.getConfig().setPriceRate(thirdPrice.divide(lastPrice,2, RoundingMode.HALF_UP));

        }
    }

    public List<AccountBalance> getAssets() {
        AccountBalance[] accountBalances = apiRestClient.getAllUserAssets(gData.getConfig().getAssetSymbol());
        return Arrays.asList(accountBalances);
    }

    public List<OrderInfo> getOpenOrder() {
        return apiRestClient.getOpenOrder(gData.getConfig().getMmSymbol(),1,200);
    }



}
