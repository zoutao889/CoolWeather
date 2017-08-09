package com.firstcode.a14_coolweather.gson;

/**
 * Created by Administrator on 2017-08-06.
 */
//当前空气质量
public class AQI {

    public AQICity city;

    public  class AQICity{
        public String aqi;
        public String pm25;
    }
}
