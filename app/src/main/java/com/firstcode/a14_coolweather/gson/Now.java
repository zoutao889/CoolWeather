package com.firstcode.a14_coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017-08-06.
 */
//当前的天气信息
public class Now {
    //气温:"tmp"
    @SerializedName("tmp")
    public String temperature;

    //描述:"cond"
    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
