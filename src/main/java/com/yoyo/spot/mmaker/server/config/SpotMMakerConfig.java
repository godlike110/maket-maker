package com.yoyo.spot.mmaker.server.config;


import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.yoyo.spot.openapi.client.YoSpotApiClientFactory;
import com.yoyo.spot.openapi.client.YoSpotApiRestClient;
import com.yoyo.spot.openapi.client.constant.SpotConstants;
import com.yoyo.spot.openapi.client.model.AccountBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wenzhiwei
 * @time 2020-02-13 14:40
 **/
@Configuration
public class SpotMMakerConfig {

    @Value("${yoyo-spot.host}")
    String host;

    @Value("${yoyo-spot.apiid}")
    String apiId;

    @Value("${yoyo-spot.privateKey}")
    String privateKey;


    @Bean("spotApiClient")
    YoSpotApiRestClient intSpotApiClient() {

        privateKey = "-----BEGIN RSA PRIVATE KEY-----\r\n" +
                "" + privateKey + "\r\n" +
                "-----END RSA PRIVATE KEY-----";

        YoSpotApiClientFactory factory = YoSpotApiClientFactory.newInstance(host,apiId, privateKey);
        YoSpotApiRestClient client = factory.newRestClient();
        return client;

    }

    @Bean("pebbleStringLoader")
    StringLoader pebbleStringLoader() {
        return new StringLoader();
    }

    @Autowired
    @Bean("pebbleStringEngine")
    PebbleEngine pebbleStringEngine(@Qualifier("pebbleStringLoader") StringLoader pebbleStringLoader) {
        PebbleEngine stringEngine = new PebbleEngine.Builder().loader(pebbleStringLoader)
                .build();
        return stringEngine;
    }

    @Bean
    FileLoader pebbleFileLoader() {
        FileLoader loader = new FileLoader();
        loader.setPrefix("/templates/");
        loader.setSuffix(".html");
        return loader;
    }

    @Bean
    @Autowired
    PebbleEngine pebbleFileEngine(@Qualifier("pebbleFileLoader") FileLoader pebbleFileLoader) {
        PebbleEngine fileEngine = new PebbleEngine.Builder().loader(pebbleFileLoader)
                .build();
        return fileEngine;
    }
}
