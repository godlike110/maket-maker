package com.yoyo.spot.mmaker.server.category;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huobi.client.SubscriptionClient;
import com.huobi.client.model.PriceDepth;
import com.huobi.client.model.event.PriceDepthEvent;
import com.huobi.client.model.event.TradeEvent;
import com.yoyo.spot.mmaker.server.config.CategoryConfig;
import com.yoyo.spot.mmaker.server.model.DataEngine;
import com.yoyo.spot.mmaker.server.model.Deph;
import com.yoyo.spot.mmaker.server.runners.MarketMaker;
import com.yoyo.spot.mmaker.server.runners.MarketUpdater;
import com.yoyo.spot.mmaker.server.runners.StatusUpdater;
import com.yoyo.spot.mmaker.server.service.SpotApiService;
import com.yoyo.spot.openapi.client.YoSpotApiRestClient;
import io.micrometer.core.instrument.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 主流币做市
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/18
 **/
//@Service
public class MainCoinMakerCategory {

    private static Logger logger = LoggerFactory.getLogger(MakerCategory.class);

    @Autowired
    private YoSpotApiRestClient spotApiService;

    @Value("classpath:/config.json")//这里指json文件路径在resources下
    private Resource configFile;



    @PostConstruct
    public void init() throws IOException {

        List<CategoryConfig> configs =  loadConfig();

        if(configs==null || configs.size()==0) {
            logger.info("MainCoinMakerCategory empty config");
        }

        for(CategoryConfig cConfig:configs) {

            DataEngine gData = new DataEngine();
            gData.setSymbol(cConfig.getThirdSymbol());
            gData.setLastTradeTime(System.currentTimeMillis());
            gData.setLastDepthTime(System.currentTimeMillis());
            gData.setDeph(new Deph());
            gData.setDepthUpdated(false);
            gData.setConfig(cConfig);

            new Thread(new MarketUpdater(gData.getConfig().getThirdEx(),gData)).start();

            StatusUpdater statusUpdator = new StatusUpdater(spotApiService,gData);
            Thread statusThread = new Thread(statusUpdator);
            statusThread.start();

            Thread mmThread = new Thread(new MarketMaker(spotApiService,gData));
            mmThread.start();
        }

    }



    private List<CategoryConfig> loadConfig() throws IOException {

        String configData =  IOUtils.toString(configFile.getInputStream(), Charset.forName("UTF-8"));
        List<CategoryConfig> categoryConfigs = JSONObject.parseArray(configData,CategoryConfig.class);
        return categoryConfigs;

    }



}
