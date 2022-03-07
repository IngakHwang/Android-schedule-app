package com.example.myapplication.java;

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

public class Fragweahter extends Fragment {

    private View view;

    private TextView frag_weather_location,frag_weather_weather, frag_weather_temp, frag_weather_time;
    private ImageView frag_weather_image;
    private TextView T1H1_date, T1H2_date, T1H3_date, T1H4_date;
    private TextView TT1H1, TT1H2, TT1H3, TT1H4;
    private ImageView SKY1_img, SKY2_img, SKY3_img, SKY4_img;
    private TextView frag_weather;

    public static Fragweahter newInstatce(){
        Fragweahter fragweahter = new Fragweahter();
        return fragweahter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_weather, container, false);

        frag_weather =(TextView) view.findViewById(R.id.frag_weather);
        frag_weather.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        frag_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri ="https://www.weather.go.kr/w/weather/forecast/short-term.do";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(uri));
                startActivity(i);
            }
        });

        frag_weather_location = (TextView) view.findViewById(R.id.frag_weather_location);
        frag_weather_weather = (TextView) view.findViewById(R.id.frag_weather_weather);
        frag_weather_temp = (TextView) view.findViewById(R.id.frag_weather_temp);
        frag_weather_time = (TextView) view.findViewById(R.id.frag_weather_time);

        T1H1_date = (TextView) view.findViewById(R.id.T1H1_date);
        T1H2_date = (TextView) view.findViewById(R.id.T1H2_date);
        T1H3_date = (TextView) view.findViewById(R.id.T1H3_date);
        T1H4_date = (TextView) view.findViewById(R.id.T1H4_date);

        TT1H1 = (TextView) view.findViewById(R.id.T1H1);
        TT1H2 = (TextView) view.findViewById(R.id.T1H2);
        TT1H3 = (TextView) view.findViewById(R.id.T1H3);
        TT1H4 = (TextView) view.findViewById(R.id.T1H4);

        SKY1_img = (ImageView) view.findViewById(R.id.SKY1_img);
        SKY2_img = (ImageView) view.findViewById(R.id.SKY2_img);
        SKY3_img = (ImageView) view.findViewById(R.id.SKY3_img);
        SKY4_img = (ImageView) view.findViewById(R.id.SKY4_img);

        SimpleDateFormat hours = new SimpleDateFormat("h");
        Date hour = new Date();
        String weather_hour = hours.format(hour);

        int weather_hourint = Integer.parseInt(weather_hour);

        String future1=Integer.toString(weather_hourint+1), future2=Integer.toString(weather_hourint+2), future3=Integer.toString(weather_hourint+3), future4=Integer.toString(weather_hourint+4);

        T1H1_date.setText(future1+" 시");

        T1H2_date.setText(future2+" 시");

        T1H3_date.setText(future3+" 시");

        T1H4_date.setText(future4+" 시");

        frag_weather_image = (ImageView) view.findViewById(R.id.frag_weather_image);

        SimpleDateFormat daytime = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date time = new Date();
        String weather_date = daytime.format(time);
        frag_weather_time.setText(weather_date);
        frag_weather_location.setText(mainAct.sido +" "+mainAct.gugun);

        if(mainAct.weatheritem.size()==40){
            //LGT 0123 4567 8 9 10 11 | 12 13 14 15 | 16 17 18 19 | 20 21 22 23
            String PTY1 = mainAct.weatheritem.get(4);
            String PTY2 = mainAct.weatheritem.get(5);
            String PTY3 = mainAct.weatheritem.get(6);
            String PTY4 = mainAct.weatheritem.get(7);

            String SKY1 = mainAct.weatheritem.get(12);
            String SKY2 = mainAct.weatheritem.get(13);
            String SKY3 = mainAct.weatheritem.get(14);
            String SKY4 = mainAct.weatheritem.get(15);

            String T1H1 = mainAct.weatheritem.get(16);
            String T1H2 = mainAct.weatheritem.get(17);
            String T1H3 = mainAct.weatheritem.get(18);
            String T1H4 = mainAct.weatheritem.get(19);

            String REH1 = mainAct.weatheritem.get(20);
            String REH2 = mainAct.weatheritem.get(21);
            String REH3 = mainAct.weatheritem.get(22);
            String REH4 = mainAct.weatheritem.get(23);

            frag_weather_temp.setText("기온 : "+T1H1+ "℃\n습도 : "+REH1+"%");
            if(PTY1.equals("0")){
                if(SKY1.equals("1")){
                    frag_weather_weather.setText("맑음");
                    frag_weather_image.setImageResource(R.drawable.weather1);
                    SKY1_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY1.equals("3")){
                    frag_weather_weather.setText("구름많음");
                    frag_weather_image.setImageResource(R.drawable.weather3);
                    SKY1_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY1.equals("4")){
                    frag_weather_weather.setText("흐림");
                    frag_weather_image.setImageResource(R.drawable.weather4);
                    SKY1_img.setImageResource(R.drawable.weather4);
                }
            }
            else if(PTY1.equals("1") || PTY1.equals("4") || PTY1.equals("5")){
                frag_weather_weather.setText("비");
                frag_weather_image.setImageResource(R.drawable.weather2);
                SKY1_img.setImageResource(R.drawable.weather2);
            }
            TT1H1.setText(T1H1+" ℃");
            TT1H2.setText(T1H2+" ℃");
            TT1H3.setText(T1H3+" ℃");
            TT1H4.setText(T1H4+" ℃");

            if(PTY2.equals("0")){
                if(SKY2.equals("1")){
                    SKY2_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY2.equals("3")){
                    SKY2_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY2.equals("4")){
                    SKY2_img.setImageResource(R.drawable.weather4);
                }
            }else if(PTY2.equals("1") || PTY2.equals("4") || PTY2.equals("5")){
                SKY2_img.setImageResource(R.drawable.weather2);
            }

            if(PTY3.equals("0")){
                if(SKY3.equals("1")){
                    SKY3_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY3.equals("3")){
                    SKY3_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY3.equals("4")){
                    SKY3_img.setImageResource(R.drawable.weather4);
                }
            }else if(PTY3.equals("1") || PTY3.equals("4") || PTY3.equals("5")){
                SKY3_img.setImageResource(R.drawable.weather2);
            }

            if(PTY4.equals("0")){
                if(SKY4.equals("1")){
                    SKY4_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY4.equals("3")){
                    SKY4_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY4.equals("4")){
                    SKY4_img.setImageResource(R.drawable.weather4);
                }
            }else if(PTY4.equals("1") || PTY2.equals("4") || PTY2.equals("5")){
                SKY4_img.setImageResource(R.drawable.weather2);
            }

        }

        if(mainAct.weatheritem.size()==50){
            // PTY 강수형태     0 없음, 1 비, 2 비/눈, 3 눈, 4 소나기, 5 빗방울, 6 빗방울/눈날림, 7 눈날림
            String PTY1 = mainAct.weatheritem.get(5);
            String PTY2 = mainAct.weatheritem.get(6);
            String PTY3 = mainAct.weatheritem.get(7);
            String PTY4 = mainAct.weatheritem.get(8);
            String PTY5 = mainAct.weatheritem.get(9);

            // SKY 하늘상태     1 맑음, 3 구름많음, 4 흐림
            String SKY1 = mainAct.weatheritem.get(15);
            String SKY2 = mainAct.weatheritem.get(16);
            String SKY3 = mainAct.weatheritem.get(17);
            String SKY4 = mainAct.weatheritem.get(18);
            String SKY5 = mainAct.weatheritem.get(19);

            // T1H 기온         ℃
            String T1H1 = mainAct.weatheritem.get(20);
            String T1H2 = mainAct.weatheritem.get(21);
            String T1H3 = mainAct.weatheritem.get(22);
            String T1H4 = mainAct.weatheritem.get(23);
            String T1H5 = mainAct.weatheritem.get(24);

            // REH 습도          %
            String REH1 = mainAct.weatheritem.get(25);
            String REH2 = mainAct.weatheritem.get(26);
            String REH3 = mainAct.weatheritem.get(27);
            String REH4 = mainAct.weatheritem.get(28);
            String REH5 = mainAct.weatheritem.get(29);

            frag_weather_temp.setText("기온 : "+T1H1+ "℃\n습도 : "+REH1+"%");
            if(PTY1.equals("0")){
                if(SKY1.equals("1")){
                    frag_weather_weather.setText("맑음");
                    frag_weather_image.setImageResource(R.drawable.weather1);
                    SKY1_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY1.equals("3")){
                    frag_weather_weather.setText("구름많음");
                    frag_weather_image.setImageResource(R.drawable.weather3);
                    SKY1_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY1.equals("4")){
                    frag_weather_weather.setText("흐림");
                    frag_weather_image.setImageResource(R.drawable.weather4);
                    SKY1_img.setImageResource(R.drawable.weather4);
                }
            }
            else if(PTY1.equals("1") || PTY1.equals("4") || PTY1.equals("5")){
                frag_weather_weather.setText("비");
                frag_weather_image.setImageResource(R.drawable.weather2);
                SKY1_img.setImageResource(R.drawable.weather2);
            }

            TT1H1.setText(T1H1+" ℃");
            TT1H2.setText(T1H2+" ℃");
            TT1H3.setText(T1H3+" ℃");
            TT1H4.setText(T1H4+" ℃");

            if(PTY2.equals("0")){
                if(SKY2.equals("1")){
                    SKY2_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY2.equals("3")){
                    SKY2_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY2.equals("4")){
                    SKY2_img.setImageResource(R.drawable.weather4);
                }
            }else if(PTY2.equals("1") || PTY2.equals("4") || PTY2.equals("5")){
                SKY2_img.setImageResource(R.drawable.weather2);
            }

            if(PTY3.equals("0")){
                if(SKY3.equals("1")){
                    SKY3_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY3.equals("3")){
                    SKY3_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY3.equals("4")){
                    SKY3_img.setImageResource(R.drawable.weather4);
                }
            }else if(PTY3.equals("1") || PTY3.equals("4") || PTY3.equals("5")){
                SKY3_img.setImageResource(R.drawable.weather2);
            }

            if(PTY4.equals("0")){
                if(SKY4.equals("1")){
                    SKY4_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY4.equals("3")){
                    SKY4_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY4.equals("4")){
                    SKY4_img.setImageResource(R.drawable.weather4);
                }
            }else if(PTY4.equals("1") || PTY2.equals("4") || PTY2.equals("5")){
                SKY4_img.setImageResource(R.drawable.weather2);
            }
        }
        if(mainAct.weatheritem.size()==60){
            // 6 7 8 9 10 11 | 18 19 20 21 22 23 | 24 25 26 27 28 29 | 30 31 32 33 34 35
            // PTY 강수형태     0 없음, 1 비, 2 비/눈, 3 눈, 4 소나기, 5 빗방울, 6 빗방울/눈날림, 7 눈날림
            String PTY1 = mainAct.weatheritem.get(6);
            String PTY2 = mainAct.weatheritem.get(7);
            String PTY3 = mainAct.weatheritem.get(8);
            String PTY4 = mainAct.weatheritem.get(9);
            String PTY5 = mainAct.weatheritem.get(10);

            // SKY 하늘상태     1 맑음, 3 구름많음, 4 흐림
            String SKY1 = mainAct.weatheritem.get(18);
            String SKY2 = mainAct.weatheritem.get(19);
            String SKY3 = mainAct.weatheritem.get(20);
            String SKY4 = mainAct.weatheritem.get(21);
            String SKY5 = mainAct.weatheritem.get(22);

            // T1H 기온         ℃
            String T1H1 = mainAct.weatheritem.get(24);
            String T1H2 = mainAct.weatheritem.get(25);
            String T1H3 = mainAct.weatheritem.get(26);
            String T1H4 = mainAct.weatheritem.get(27);
            String T1H5 = mainAct.weatheritem.get(28);

            // REH 습도          %
            String REH1 = mainAct.weatheritem.get(30);
            String REH2 = mainAct.weatheritem.get(31);
            String REH3 = mainAct.weatheritem.get(32);
            String REH4 = mainAct.weatheritem.get(33);
            String REH5 = mainAct.weatheritem.get(34);

            frag_weather_temp.setText("기온 : "+T1H1+ "℃\n습도 : "+REH1+"%");
            if(PTY1.equals("0")){
                if(SKY1.equals("1")){
                    frag_weather_weather.setText("맑음");
                    frag_weather_image.setImageResource(R.drawable.weather1);
                    SKY1_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY1.equals("3")){
                    frag_weather_weather.setText("구름많음");
                    frag_weather_image.setImageResource(R.drawable.weather3);
                    SKY1_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY1.equals("4")){
                    frag_weather_weather.setText("흐림");
                    frag_weather_image.setImageResource(R.drawable.weather4);
                    SKY1_img.setImageResource(R.drawable.weather4);
                }
            }
            else if(PTY1.equals("1") || PTY1.equals("4") || PTY1.equals("5")){
                frag_weather_weather.setText("비");
                frag_weather_image.setImageResource(R.drawable.weather2);
                SKY1_img.setImageResource(R.drawable.weather2);
            }

            TT1H1.setText(T1H1+" ℃");
            TT1H2.setText(T1H2+" ℃");
            TT1H3.setText(T1H3+" ℃");
            TT1H4.setText(T1H4+" ℃");

            if(PTY2.equals("0")){
                if(SKY2.equals("1")){
                    SKY2_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY2.equals("3")){
                    SKY2_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY2.equals("4")){
                    SKY2_img.setImageResource(R.drawable.weather4);
                }
            }else if(PTY2.equals("1") || PTY2.equals("4") || PTY2.equals("5")){
                SKY2_img.setImageResource(R.drawable.weather2);
            }

            if(PTY3.equals("0")){
                if(SKY3.equals("1")){
                    SKY3_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY3.equals("3")){
                    SKY3_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY3.equals("4")){
                    SKY3_img.setImageResource(R.drawable.weather4);
                }
            }else if(PTY3.equals("1") || PTY3.equals("4") || PTY3.equals("5")){
                SKY3_img.setImageResource(R.drawable.weather2);
            }

            if(PTY4.equals("0")){
                if(SKY4.equals("1")){
                    SKY4_img.setImageResource(R.drawable.weather1);
                }
                else if(SKY4.equals("3")){
                    SKY4_img.setImageResource(R.drawable.weather3);
                }
                else if(SKY4.equals("4")){
                    SKY4_img.setImageResource(R.drawable.weather4);
                }
            }else if(PTY4.equals("1") || PTY2.equals("4") || PTY2.equals("5")){
                SKY4_img.setImageResource(R.drawable.weather2);
            }
        }

        return view;
    }


}
