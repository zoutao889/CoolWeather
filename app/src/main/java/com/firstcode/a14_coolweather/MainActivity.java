package com.firstcode.a14_coolweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.firstcode.a14_coolweather.db.Province;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public TextView text1;
    ArrayList<Province> provinces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
