package com.yoyo.spot.mmaker.server;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;


import java.io.Closeable;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author wenzhiwei
 * @time 2019-05-07 15:08
 **/
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
@EnableScheduling
//@MapperScan(basePackages = {"com.yoyo.spot.mmaker.server.dao"})
public class App {

    public static void main(String[] args) {

        SpringApplication.run(App.class, args);
    }

}
