package com.firstcode.a14_coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-08-01.
 */

public class County extends DataSupport {

    private int id;
    //countyName记录县的名字
    private String countyName;
    //weatherld记录县所对应的天气id
    private String weatherId;
    //cityld记录当前县所属市的id值
    private int cityId;

    public int getId() {
        return id;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
