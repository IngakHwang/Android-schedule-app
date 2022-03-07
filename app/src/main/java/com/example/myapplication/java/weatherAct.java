package com.example.myapplication.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;

import com.example.myapplication.R;
import com.example.myapplication.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class weatherAct extends AppCompatActivity {

    // static ArrayList<String> weatheritem = new ArrayList<>();
    // static int weatheri =0;

    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        setTitle("날씨 / 미세먼지 / 코로나");


       /*String weahterurl = weatherurl();

        WeatherAPI weatherapi = new WeatherAPI(weahterurl);

        weatherapi.execute();*/

        ViewPager viewPager = findViewById(R.id.weather_viewpager);
        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        TabLayout tabLayout = findViewById(R.id.weather_tablayout);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    public String weatherurl(){
        String serviceKey ="%2F3mLJj4iwDg2oJCwR9ybHw1VUyOn5RoS849b2NBoODNMPVe7gGnNNCkgw3p%2FWrAsR4h1MHQab0eWs2YjozcVaQ%3D%3D";
        String numOfRows = "10";
        String pageNo = "1";

        SimpleDateFormat real_time = new SimpleDateFormat("yyyyMMdd");

        Date time = new Date();

        String base_date = real_time.format(time);

        String base_time = "0500";
        String nx = "61";
        String ny = "129";

        String weatherurl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey="+serviceKey+"&pageNo="+pageNo+"&numOfRows="+numOfRows+"&dataType=XML"+"&base_date="+base_date
                +"&base_time="+base_time+"&nx="+nx+"&ny="+ny;

        return weatherurl;
    }
}