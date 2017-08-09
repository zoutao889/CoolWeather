package com.firstcode.a14_coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firstcode.a14_coolweather.db.City;
import com.firstcode.a14_coolweather.db.County;
import com.firstcode.a14_coolweather.db.Province;
import com.firstcode.a14_coolweather.util.HttpUtil;
import com.firstcode.a14_coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017-08-03.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    //适配器:
    private ArrayAdapter<String> adapter;
    //数据源:
    private List<String> dataList = new ArrayList<>();
    //省列表:
    private List<Province> provinceList;
    //市列表:
    private List<City> cityList;
    //县列表:
    private List<County> countyList;
    //选中省份:
    private Province selectedProvince;
    //选中城市:
    private City selectedCity;
    //当前选中级别
    private int currentLevel;

    //为碎片创建视图（加载布局）时调用
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载布局文件:
        View view = inflater.inflate(R.layout.choose_area, container, false);
        //获取TextView控件:
        titleText = ((TextView) view.findViewById(R.id.title_text));
        //获取返回按钮控件:
        backButton = ((Button) view.findViewById(R.id.back_button));
        //获取ListView控件:
        listView = ((ListView) view.findViewById(R.id.list_view));
        //初始化adapter:
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        //设置adapter:
        listView.setAdapter(adapter);
        return view;
    }

    //确保与碎片相关联的活动一定已经创建完毕的时候调用
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //ListView点击事件:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    //加载市级数据:
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    //加载县级数据:
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    //如果当前级别是LEVEL_COUNTY，就启动WeatherAaivity
                    //并把当前选中县盼天气id传递过去
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        //返回按钮点击事件:
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    //加载市级数据:
                    queryCities();
                } else if (currentLevel == LEVEL_CITY){
                    //加载省级数据:
                    queryProvinces();
                }
            }
        });
        //加载省级数据:
        queryProvinces();
    }

    //加载省级数据:
    //查询全国所有省,优先从数据库查询:如果没有查询到再去服务器上查询
    private void queryProvinces() {
        //将头布局的标题设置成中国
        titleText.setText("中国");
        //将返回按钮隐藏起来
        backButton.setVisibility(View.GONE);

        //调用LitePal的查询接口来从数据库中读取省级数据:
        //如果读取到:直接将数据显示到界面上
        //没读取到:向服务区请求数据
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            //刷新adapter:
            adapter.notifyDataSetChanged();
            //将第position个item显示在listView的最上面一项
            listView.setSelection(0);
            //设置当前级别:
            currentLevel = LEVEL_PROVINCE;
        }else {
            //组装出一个请求地址:请求省级数据
            String address = "http://guolin.tech/api/china";
            queryFromServier(address,"province");
        }
    }

    //查询选中省内所有市,优先从数据库查询:如果没有查询到再去服务器上查询
    private void queryCities() {
        //显示省名字:
        titleText.setText(selectedProvince.getProvinceName());
        //显示返回按钮:
        backButton.setVisibility(View.VISIBLE);
        //查询省内所有市:
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        //判断查询是否成功:
        if (cityList.size() > 0){
            //清空数据源:
            dataList.clear();
            //添加省内所有市到dataList:
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            //更新adapter:
            adapter.notifyDataSetChanged();
            //显示第一个位置:
            listView.setSelection(0);
            //更新当前显示级别:
            currentLevel = LEVEL_CITY;
        }else{
            //获取省号:
            int provinceCode = selectedProvince.getProvinceCode();
            //拼接省内市url地址:
            String address = "http://guolin.tech/api/china/" + provinceCode;
            //服务器请求省内所有市:
            queryFromServier(address,"city");
        }
    }

    //查询选中市内所有县,优先从数据库查询,如果没有查询到再去服务器上查询:
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServier(address,"county");
        }
    }


    //根据传入地址和类型从服务器上查询省市县数据:
    //参数:
        //请求地址:
            //"province"
            //"city"
            //"county"
        //请求项目:
            //"province"
            //"city"
            //"county"
    private void queryFromServier(String address,final String type){
        //显示进度条对话框:
        showProgressDialog();
        //调用HttpUtil的sendOkHttpRequest()方法来向服务器发送请求:
        //响应的数据会回调到onResponse()方法中
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Response转为String:
                String responseText = response.body().string();
                boolean result = false;
                //解析数据:
                if ("province".equals(type)){
                    //解析省:调用Utility的handleProvincesResponse()方法来解析和处理服务器返回的数据，并存储到数据库中
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    //解析市:
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    //解析县:
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                //主线程更新UI:
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭进度对话框:
                            closeProgressDialog();
                            //
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //显示进度对话框:
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭进度对话框:
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
