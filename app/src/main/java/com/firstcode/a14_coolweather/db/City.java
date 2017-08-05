package com.firstcode.a14_coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-08-01.
 */

public class City extends DataSupport {

    private int id;
    //cityName记录市的名字
    private String cityName;
    //cityCode记录市的代号
    private int cityCode;
    //provinceId记录当前市所属省的id值
    private int provinceId;

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }
}
