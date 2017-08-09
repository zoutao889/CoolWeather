package com.firstcode.a14_coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017-08-06.
 */
//城市基本信息:
public class Basic {

    //城市名:"city"
    //JSON字段"city"与Java字段"cityName"建立映射关系:
    @SerializedName("city")
    public String cityName;

    //城市对应的天气id:"id"
    //JSON字段"id"与Java字段"weatherId"之间建立映射关系:
    @SerializedName("id")
    public String weatherId;

    //
    public Update update;

    //update中的loc表示天气的更新时间:"loc"
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
