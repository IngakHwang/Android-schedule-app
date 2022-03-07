package com.example.myapplication.java;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragdust extends Fragment {

    private TextView frag_dust_location, frag_dust_pm10,frag_dust_pm10value, frag_dust_pm25,frag_dust_pm25value, frag_dust_time;
    private ImageView frag_dust_pm10img, frag_dust_pm25img;
    private TextView frag_dust_futuredate1, frag_dust_futuredate2, frag_dust_futuredate3, frag_dust_futuredate4, frag_dust_futuredate5;
    private TextView frag_dust_future1, frag_dust_future2, frag_dust_future3, frag_dust_future4, frag_dust_future5;
    private ImageView frag_dust_futureimg1, frag_dust_futureimg2, frag_dust_futureimg3, frag_dust_futureimg4, frag_dust_futureimg5;
    private TextView frag_dust;

    private View view;

    public static Fragdust newInstatce(){
        Fragdust fragdust = new Fragdust();
        return fragdust;
    }

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_dust, container, false);

        frag_dust = (TextView)view.findViewById(R.id.frag_dust);
        frag_dust.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        frag_dust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri ="https://www.airkorea.or.kr/web";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(uri));
                startActivity(i);
            }
        });

        SimpleDateFormat daytime = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date time = new Date();
        String dust_date = daytime.format(time);

        SimpleDateFormat hours = new SimpleDateFormat("h");
        Date hour = new Date();
        String dust_hour = hours.format(hour);

        frag_dust_location = (TextView) view.findViewById(R.id.frag_dust_location);
        frag_dust_pm10 = (TextView) view.findViewById(R.id.frag_dust_pm10);
        frag_dust_pm25 = (TextView) view.findViewById(R.id.frag_dust_pm25);
        frag_dust_pm10value = (TextView) view.findViewById(R.id.frag_dust_pm10value);
        frag_dust_pm25value = (TextView) view.findViewById(R.id.frag_dust_pm25value);
        frag_dust_time = (TextView) view.findViewById(R.id.frag_dust_time);
        frag_dust_pm10img = (ImageView) view.findViewById(R.id.frag_dust_pm10img);
        frag_dust_pm25img = (ImageView) view.findViewById(R.id.frag_dust_pm25img);

        frag_dust_futuredate1 = (TextView) view.findViewById(R.id.frag_dust_futuredate1);
        frag_dust_futuredate2 = (TextView) view.findViewById(R.id.frag_dust_futuredate2);
        frag_dust_futuredate3 = (TextView) view.findViewById(R.id.frag_dust_futuredate3);
        frag_dust_futuredate4 = (TextView) view.findViewById(R.id.frag_dust_futuredate4);
        frag_dust_futuredate5 = (TextView) view.findViewById(R.id.frag_dust_futuredate5);

        frag_dust_future1 = (TextView) view.findViewById(R.id.frag_dust_future1);
        frag_dust_future2 = (TextView) view.findViewById(R.id.frag_dust_future2);
        frag_dust_future3 = (TextView) view.findViewById(R.id.frag_dust_future3);
        frag_dust_future4 = (TextView) view.findViewById(R.id.frag_dust_future4);
        frag_dust_future5 = (TextView) view.findViewById(R.id.frag_dust_future5);

        frag_dust_futureimg1 = (ImageView) view.findViewById(R.id.frag_dust_futureimg1);
        frag_dust_futureimg2 = (ImageView) view.findViewById(R.id.frag_dust_futureimg2);
        frag_dust_futureimg3 = (ImageView) view.findViewById(R.id.frag_dust_futureimg3);
        frag_dust_futureimg4 = (ImageView) view.findViewById(R.id.frag_dust_futureimg4);
        frag_dust_futureimg5 = (ImageView) view.findViewById(R.id.frag_dust_futureimg5);

        String pm10 = mainAct.dustitem.get(25);
        String pm25 = mainAct.dustitem.get(26);


        frag_dust_futuredate1.setText(dust_hour+" 시");
        int dust_hourint = Integer.parseInt(dust_hour);
        String futuredate2=Integer.toString(dust_hourint+1), futuredate3=Integer.toString(dust_hourint+2) , futuredate4=Integer.toString(dust_hourint+3), futuredate5=Integer.toString(dust_hourint+4);

        frag_dust_futuredate2.setText(futuredate2+" 시");

        frag_dust_futuredate3.setText(futuredate3+" 시");

        frag_dust_futuredate4.setText(futuredate4+" 시");

        frag_dust_futuredate5.setText(futuredate5+" 시");

        int pm10int = Integer.parseInt(pm10);
        int pm25int = Integer.parseInt(pm25);
        if(pm10int<=50){
            frag_dust_pm10value.setText("좋음 "+pm10);
            frag_dust_pm10value.setTextColor(R.color.skyblue);
            frag_dust_pm10.setTextColor(R.color.skyblue);
            frag_dust_pm10img.setImageResource(R.drawable.dust1);
        }
        else if(pm10int>50 && pm10int<=100){
            frag_dust_pm10value.setText("보통 "+pm10);
            frag_dust_pm10value.setTextColor(R.color.green);
            frag_dust_pm10.setTextColor(R.color.green);
            frag_dust_pm10img.setImageResource(R.drawable.dust2);
        }
        else if(pm10int>100 && pm10int<=200){
            frag_dust_pm10value.setText("나쁨 "+pm10);
            frag_dust_pm10value.setTextColor(R.color.orange);
            frag_dust_pm10.setTextColor(R.color.orange);
            frag_dust_pm10img.setImageResource(R.drawable.dust3);
        }
        else if(pm10int>200 && pm10int<=300){
            frag_dust_pm10value.setText("매우나쁨 "+pm10);
            frag_dust_pm10value.setTextColor(R.color.red);
            frag_dust_pm10.setTextColor(R.color.red);
            frag_dust_pm10img.setImageResource(R.drawable.dust4);
        }
        else if(pm10int>300 && pm10int<=10000){
            frag_dust_pm10value.setText("위험 "+pm10);
            frag_dust_pm10value.setTextColor(R.color.black);
            frag_dust_pm10.setTextColor(R.color.black);
            frag_dust_pm10img.setImageResource(R.drawable.dust5);
        }

        if(pm25int<=50){
            frag_dust_pm25value.setText("좋음 "+pm25);
            frag_dust_pm25value.setTextColor(R.color.skyblue);
            frag_dust_pm25.setTextColor(R.color.skyblue);
            frag_dust_pm25img.setImageResource(R.drawable.dust1);
        }
        else if(pm25int>50 && pm25int<=100){
            frag_dust_pm25value.setText("보통 "+pm25);
            frag_dust_pm25value.setTextColor(R.color.green);
            frag_dust_pm25.setTextColor(R.color.green);
            frag_dust_pm25img.setImageResource(R.drawable.dust2);
        }
        else if(pm25int>100 && pm25int<=200){
            frag_dust_pm25value.setText("나쁨 "+pm25);
            frag_dust_pm25value.setTextColor(R.color.orange);
            frag_dust_pm25.setTextColor(R.color.orange);
            frag_dust_pm25img.setImageResource(R.drawable.dust3);
        }
        else if(pm25int>200 && pm25int<=300){
            frag_dust_pm25value.setText("매우나쁨 "+pm25);
            frag_dust_pm25value.setTextColor(R.color.red);
            frag_dust_pm25.setTextColor(R.color.red);
            frag_dust_pm25img.setImageResource(R.drawable.dust4);
        }
        else if(pm25int>300 && pm25int<=10000){
            frag_dust_pm25value.setText("위험 "+pm25);
            frag_dust_pm25value.setTextColor(R.color.black);
            frag_dust_pm25.setTextColor(R.color.black);
            frag_dust_pm25img.setImageResource(R.drawable.dust5);
        }

        String future1 = mainAct.dustitem.get(25);
        int futrue1int = Integer.parseInt(future1);

        if(futrue1int<=50){
            frag_dust_future1.setText("좋음");
            frag_dust_futuredate1.setTextColor(R.color.skyblue);
            frag_dust_future1.setTextColor(R.color.skyblue);
            frag_dust_futureimg1.setImageResource(R.drawable.dust1);
        }
        else if(futrue1int>50 && futrue1int<=100){
            frag_dust_future1.setText("보통");
            frag_dust_futuredate1.setTextColor(R.color.green);
            frag_dust_future1.setTextColor(R.color.green);
            frag_dust_futureimg1.setImageResource(R.drawable.dust2);
        }

        else if(futrue1int>100 && futrue1int<=200){
            frag_dust_future1.setText("나쁨");
            frag_dust_futuredate1.setTextColor(R.color.orange);
            frag_dust_future1.setTextColor(R.color.orange);
            frag_dust_futureimg1.setImageResource(R.drawable.dust3);
        }
        else if(futrue1int>200 && futrue1int<=300){
            frag_dust_future1.setText("매우나쁨");
            frag_dust_futuredate1.setTextColor(R.color.red);
            frag_dust_future1.setTextColor(R.color.red);
            frag_dust_futureimg1.setImageResource(R.drawable.dust4);
        }
        else if(futrue1int>300 && futrue1int<=10000){
            frag_dust_future1.setText("위험");
            frag_dust_future1.setTextColor(R.color.black);
            frag_dust_futuredate1.setTextColor(R.color.black);
            frag_dust_futureimg1.setImageResource(R.drawable.dust5);
        }

        String future2 = mainAct.dustitem.get(22);
        int future2int = Integer.parseInt(future2);
        if(future2int<=50){
            frag_dust_future2.setText("좋음");
            frag_dust_future2.setTextColor(R.color.skyblue);
            frag_dust_futuredate2.setTextColor(R.color.skyblue);
            frag_dust_futureimg2.setImageResource(R.drawable.dust1);
        }
        else if(future2int>50 && future2int<=100){
            frag_dust_future2.setText("보통");
            frag_dust_future2.setTextColor(R.color.green);
            frag_dust_futuredate2.setTextColor(R.color.green);
            frag_dust_futureimg2.setImageResource(R.drawable.dust2);
        }

        else if(future2int>100 && future2int<=200){
            frag_dust_future2.setText("나쁨");
            frag_dust_future2.setTextColor(R.color.orange);
            frag_dust_futuredate2.setTextColor(R.color.orange);
            frag_dust_futureimg2.setImageResource(R.drawable.dust3);
        }
        else if(future2int>200 && future2int<=300){
            frag_dust_future2.setText("매우나쁨");
            frag_dust_future2.setTextColor(R.color.red);
            frag_dust_futuredate2.setTextColor(R.color.red);
            frag_dust_futureimg2.setImageResource(R.drawable.dust4);
        }
        else if(future2int>300 && future2int<=10000){
            frag_dust_future2.setText("위험");
            frag_dust_future2.setTextColor(R.color.black);
            frag_dust_futuredate2.setTextColor(R.color.black);
            frag_dust_futureimg2.setImageResource(R.drawable.dust5);
        }

        String future3 = mainAct.dustitem.get(19);
        int future3int = Integer.parseInt(future3);
        if(future3int<=50){
            frag_dust_future3.setText("좋음");
            frag_dust_future3.setTextColor(R.color.skyblue);
            frag_dust_futuredate3.setTextColor(R.color.skyblue);
            frag_dust_futureimg3.setImageResource(R.drawable.dust1);
        }
        else if(future3int>50 && future3int<=100){
            frag_dust_future3.setText("보통");
            frag_dust_future3.setTextColor(R.color.green);
            frag_dust_futuredate3.setTextColor(R.color.green);
            frag_dust_futureimg3.setImageResource(R.drawable.dust2);
        }

        else if(future3int>100 && future3int<=200){
            frag_dust_future3.setText("나쁨");
            frag_dust_future3.setTextColor(R.color.orange);
            frag_dust_futuredate3.setTextColor(R.color.orange);
            frag_dust_futureimg3.setImageResource(R.drawable.dust3);
        }
        else if(future3int>200 && future3int<=300){
            frag_dust_future3.setText("매우나쁨");
            frag_dust_future3.setTextColor(R.color.red);
            frag_dust_futuredate3.setTextColor(R.color.red);
            frag_dust_futureimg3.setImageResource(R.drawable.dust4);
        }
        else if(future3int>300 && future3int<=10000){
            frag_dust_future3.setText("위험");
            frag_dust_future3.setTextColor(R.color.black);
            frag_dust_futuredate3.setTextColor(R.color.black);
            frag_dust_futureimg3.setImageResource(R.drawable.dust5);
        }

        String future4 = mainAct.dustitem.get(16);
        int future4int = Integer.parseInt(future4);
        if(future4int<=50){
            frag_dust_future4.setText("좋음");
            frag_dust_future4.setTextColor(R.color.skyblue);
            frag_dust_futuredate4.setTextColor(R.color.skyblue);
            frag_dust_futureimg4.setImageResource(R.drawable.dust1);
        }
        else if(future4int>50 && future4int<=100){
            frag_dust_future4.setText("보통");
            frag_dust_future4.setTextColor(R.color.green);
            frag_dust_futuredate4.setTextColor(R.color.green);
            frag_dust_futureimg4.setImageResource(R.drawable.dust2);
        }

        else if(future4int>100 && future4int<=200){
            frag_dust_future4.setText("나쁨");
            frag_dust_future4.setTextColor(R.color.orange);
            frag_dust_futuredate4.setTextColor(R.color.orange);
            frag_dust_futureimg4.setImageResource(R.drawable.dust3);
        }
        else if(future4int>200 && future4int<=300){
            frag_dust_future4.setText("매우나쁨");
            frag_dust_future4.setTextColor(R.color.red);
            frag_dust_futuredate4.setTextColor(R.color.red);
            frag_dust_futureimg4.setImageResource(R.drawable.dust4);
        }
        else if(future4int>300 && future4int<=10000){
            frag_dust_future4.setText("위험");
            frag_dust_future4.setTextColor(R.color.black);
            frag_dust_futuredate4.setTextColor(R.color.black);
            frag_dust_futureimg4.setImageResource(R.drawable.dust5);
        }

        String future5 = mainAct.dustitem.get(13);
        int future5int = Integer.parseInt(future5);
        if(future5int<=50){
            frag_dust_future5.setText("좋음");
            frag_dust_future5.setTextColor(R.color.skyblue);
            frag_dust_futuredate5.setTextColor(R.color.skyblue);
            frag_dust_futureimg5.setImageResource(R.drawable.dust1);
        }
        else if(future5int>50 && future5int<=100){
            frag_dust_future5.setText("보통");
            frag_dust_future5.setTextColor(R.color.green);
            frag_dust_futuredate5.setTextColor(R.color.green);
            frag_dust_futureimg5.setImageResource(R.drawable.dust2);
        }

        else if(future5int>100 && future5int<=200){
            frag_dust_future5.setText("나쁨");
            frag_dust_future5.setTextColor(R.color.orange);
            frag_dust_futuredate5.setTextColor(R.color.orange);
            frag_dust_futureimg5.setImageResource(R.drawable.dust3);
        }
        else if(future5int>200 && future5int<=300){
            frag_dust_future5.setText("매우나쁨");
            frag_dust_future5.setTextColor(R.color.red);
            frag_dust_futuredate5.setTextColor(R.color.red);
            frag_dust_futureimg5.setImageResource(R.drawable.dust4);
        }
        else if(future5int>300 && future5int<=10000){
            frag_dust_future5.setText("위험");
            frag_dust_future5.setTextColor(R.color.black);
            frag_dust_futuredate5.setTextColor(R.color.black);
            frag_dust_futureimg5.setImageResource(R.drawable.dust5);
        }

        frag_dust_time.setText(dust_date);

        frag_dust_location.setText(mainAct.sido +" "+mainAct.gugun);

        return view;
    }
}
