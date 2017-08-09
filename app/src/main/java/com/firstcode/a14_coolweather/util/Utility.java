package com.firstcode.a14_coolweather.util;

import android.text.TextUtils;

import com.firstcode.a14_coolweather.db.City;
import com.firstcode.a14_coolweather.db.County;
import com.firstcode.a14_coolweather.db.Province;
import com.firstcode.a14_coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-08-02.
 */

public class Utility {

    /**
     * 解析出来服务器返回省级数据:
     */
    public static boolean handleProvinceResponse(String response){

        if (!TextUtils.isEmpty(response)){
            try{
                //创建JSON数组: [{"id":1,"name":"北京"},{"id":2,"name":"上海"},...,{"id":34,"name":"新疆"}]
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0;i<allProvinces.length(); i++){
                    //获取JSON对象: {"id":1,"name":"北京"}
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    //创建province对象:
                    Province province = new Province();
                    //获取name:   "name":"北京"
                    province.setProvinceName(provinceObject.getString("name"));
                    //获取id: "id":1
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //调用save()方法将数据存储到数据库当中
                    province.save();
                }
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回市级数据:
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try{
                //创建JSON数组: [{"id":113,"name":"南京"},...,{"id":125,"name":"宿迁"}]
                JSONArray allCities = new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    //{"id":113,"name":"南京"}
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    //"name":"南京"
                    city.setCityName(cityObject.getString("name"));
                    //"id":113
                    city.setCityCode(cityObject.getInt("id"));
                    //设置升级ID:
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回县级数据:
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try{
                //[{"id":937,"name":"苏州","weather_id":"CN101190401"},...]
                JSONArray allCounties = new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    //{"id":937,"name":"苏州","weather_id":"CN101190401"}
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    //"name":"苏州"
                    county.setCountyName(countyObject.getString("name"));
                    //"weather_id":"CN101190401"
                    county.setWeatherId(countyObject.getString("weather_id"));
                    //市级ID:
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析天气JSON数据的方法
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            //直接将JSON数据转换成Weather对象:
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
