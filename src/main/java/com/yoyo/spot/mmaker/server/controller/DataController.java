package com.yoyo.spot.mmaker.server.controller;


import com.yoyo.spot.mmaker.server.service.SpotApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.POST;


/**
 * @author wenzhiwei
 * @time 2020-02-13 15:11
 **/
@RequestMapping("/")
@RestController
public class DataController {

    private static Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private SpotApiService spotApiService;


}
