package com.firstcode.a14_coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-08-01.
 */
//LitePal中的每一个实体类都是必须要继承自DataSupport类
public class Province extends DataSupport{
    //id是每个实体类中都应该有的字段
    private int id;
    //provinceName记录省的名字
    private String provinceName;
    //provinceCode记录省的代号
    private int provinceCode;

    public int getId() {
        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
