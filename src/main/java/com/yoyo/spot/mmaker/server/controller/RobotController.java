package com.yoyo.spot.mmaker.server.controller;



import com.yoyo.spot.mmaker.server.service.SpotApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author wenzhiwei
 * @time 2020-02-13 15:11
 **/
@RequestMapping("/robot")
@RestController
public class RobotController {

    private static Logger logger = LoggerFactory.getLogger(RobotController.class);

    @Autowired
    private SpotApiService spotApiService;


    @GetMapping("cacelAll")
    public Boolean control(@RequestParam("symbol") String symbol) {
        spotApiService.cancelAll("GGC/USDT");
        return true;
    }


}
