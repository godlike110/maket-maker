package com.yoyo.spot.mmaker.server.util;

/**
 * author:zhiwei
 * <p>
 * email:597384114@qq.com
 * <p>
 * time:2020/2/19
 **/

public class MathUtil {

    /**
     * 正太分布
     * @param u
     * @param v
     * @return
     */
    public static double normal(float u, float v){
        java.util.Random random = new java.util.Random();
        return Math.sqrt(v)*random.nextGaussian()+u;
    }

}
