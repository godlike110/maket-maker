package com.yoyo.spot.mmaker.server.enums;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/14
 **/

public enum  Direction {

    LONG("LONG"),
    SHORT("SHORT");

    private String direction;

    Direction(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
