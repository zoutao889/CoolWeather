package com.firstcode.a14_coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-08-01.
 */

public class City extends DataSupport {

    private int id;
    private String cityName;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
