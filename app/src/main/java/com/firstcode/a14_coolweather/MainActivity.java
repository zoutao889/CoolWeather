package com.firstcode.a14_coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //从SharedPreferences文件中读取缓存数据:
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //如果不为null就说明之前已经请求过天气数据了:
        //那么就没必要让用户再次选择城市,直接跳转到WeatherActivity即可:
        if (prefs.getString("weather", null) != null){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
