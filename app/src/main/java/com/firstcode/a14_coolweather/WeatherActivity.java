package com.firstcode.a14_coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firstcode.a14_coolweather.gson.Forecast;
import com.firstcode.a14_coolweather.gson.Weather;
import com.firstcode.a14_coolweather.util.HttpUtil;
import com.firstcode.a14_coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public ScrollView weatherLayout;
    public TextView titleCity;
    public TextView titleUpdateTime;
    public TextView degreeText;
    public TextView weatherInfoText;
    public LinearLayout forecastLayout;
    public TextView aqiText;
    public TextView pm25Text;
    public TextView comfortText;
    public TextView carWashText;
    public TextView sportText;
    public ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    public Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //5.0及以上系统时才会执行后面的代码:
        if (Build.VERSION.SDK_INT >= 21){
            //调用了getWindow() .getDecorView()方法拿到当前活动的DecorView
            View decorView = getWindow().getDecorView();
            //调用它的setSystemUiVisibility()方法来改变系统UI的显示
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        //初始化各控件:
        weatherLayout = ((ScrollView) findViewById(R.id.weather_layout));
        titleCity = ((TextView) findViewById(R.id.title_city));

        titleUpdateTime = ((TextView) findViewById(R.id.title_update_time));
        degreeText = ((TextView) findViewById(R.id.degree_text));
        weatherInfoText = ((TextView) findViewById(R.id.weather_info_text));

        forecastLayout = ((LinearLayout) findViewById(R.id.forecast_layout));
        aqiText = ((TextView) findViewById(R.id.aqi_text));
        pm25Text = ((TextView) findViewById(R.id.pm25_text));

        comfortText = ((TextView) findViewById(R.id.comfort_text));
        carWashText = ((TextView) findViewById(R.id.car_wash_text));
        sportText = ((TextView) findViewById(R.id.sport_text));
        //加载背景图片控件:
        bingPicImg = ((ImageView) findViewById(R.id.bing_pic_img));

        swipeRefresh = ((SwipeRefreshLayout) findViewById(R.id.swipe_refresh));
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        //获取到新增的DrawerLayout和Button的实例:
        drawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));
        navButton = ((Button) findViewById(R.id.nav_button));

        //尝试从本地缓存中读取天气数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        final String weatherId;
        if (weatherString != null){
            //有缓存时直接解析天气数据:
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时服务器查询天气:
            //从Intent中取出天气id
            weatherId = getIntent().getStringExtra("weather_id");
            //将ScrollView进行隐藏
            weatherLayout.setVisibility(View.INVISIBLE);
            //调用requestWeather()方法来从服务器请求天气数据
            requestWeather(weatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //在Button的点击事件中调用DrawerLayout的openDrawer()方法来打开滑动菜单
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }

    }

    //根据天气id请求城市天气信息:
    //参数:天气id
    public void requestWeather(final String weatherId){
        //参数中传入的天气id和我们之前申请好的API Key拼装出—个接口地址:
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=54df490de8d5450ba029f0c59314c83f";
        //调用HttpUtil.sendOkHttpRequest()方法来向该地址发出请求:
        //服务器会将相应城市的天气信息以JSON格式返回:
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            //onResponse()回调中先调用Utility.handleWeatherRespanse()方法将返回的JSON数据转挽盛Weather对象:
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //再将当前线程切换到主线程:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //进行判断:
                            //status状态是ok:
                                //说明请求天气成功了
                                //将返回的数据缓存到SharedPreferences当中
                                //调用showWeatherlnfo()方法来进行内容显示
                            //status状态不是ok
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });
        loadBingPic();
    }

    //处理并展示Weather实体类中数据:
    private void showWeatherInfo(Weather weather){
        //从Weather对象中获取数据:
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;
        //显示到相应的控件上
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        //未来几天天气预报的部分:for循环来处理每天的天气信息
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList){
            //循环中动态加载forecast_item.xml布局并设置相应的数据到父布局中:
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);

            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            //添加到父布局当中
            forecastLayout.addView(view);
        }
        if (weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度:" + weather.suggestion.comfort.info;
        String carWash = "洗车指数:" + weather.suggestion.carWash.info;
        String sport = "运动建议:" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        //设置完了所有数据之后，记得要将ScrollView重新变成可见
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 加载必应每日一图:
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

}
