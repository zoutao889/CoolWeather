package com.firstcode.a14_coolweather.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017-08-02.
 */

public class HttpUtil {
    //发起一条HTTP请求:
    //1.调用sendOkHttpRequest()方法
    //2.传入请求地址
    //3.注册一个回调来处理服务器响应
    public static void sendOkHttpRequest(String address, Callback callback){
        //创建1个Request对象:
        OkHttpClient client = new OkHttpClient();
        //通过url()方法设置目标网络地址:
        Request request = new Request.Builder().url(address).build();
        //newCall()方法来创建一个Call对象
        //enqueue方法注册callback对象处理服务器返回数据:
        client.newCall(request).enqueue(callback);
    }
}
