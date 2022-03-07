package com.example.myapplication.java;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragcovid extends Fragment {

    private View view;

    private TextView frag_covid_decideCnt, frag_covid_decideupdown, frag_covid_examCnt, frag_covid_examupdown, frag_covid_clearCnt, frag_covid_clearupdown, frag_covid_deathCnt, frag_covid_deathupdown,frag_covid_date,frag_covid_todaydecide;

    private TextView frag_covid;

    public static Fragcovid newInstatce(){
        Fragcovid fragcovid = new Fragcovid();
        return fragcovid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_covid, container, false);

        frag_covid = (TextView) view.findViewById(R.id.frag_covid);
        frag_covid.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        frag_covid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri ="http://ncov.mohw.go.kr/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(uri));
                startActivity(i);
            }
        });

        // 확진자
        frag_covid_decideCnt = (TextView) view.findViewById(R.id.frag_covid_decideCnt);
        frag_covid_decideupdown = (TextView) view.findViewById(R.id.frag_covid_decideupdown);

        //검사중
        frag_covid_examCnt = (TextView) view.findViewById(R.id.frag_covid_examCnt);
        frag_covid_examupdown = (TextView) view.findViewById(R.id.frag_covid_examupdown);

        //격리해제
        frag_covid_clearCnt = (TextView) view.findViewById(R.id.frag_covid_clearCnt);
        frag_covid_clearupdown = (TextView) view.findViewById(R.id.frag_covid_clearupdown);

        //사망자
        frag_covid_deathCnt = (TextView) view.findViewById(R.id.frag_covid_deathCnt);
        frag_covid_deathupdown = (TextView) view.findViewById(R.id.frag_covid_deathupdown);

        frag_covid_date = (TextView) view.findViewById(R.id.frag_covid_date);

        frag_covid_todaydecide = (TextView) view.findViewById(R.id.frag_covid_todaydecide);

        SimpleDateFormat coviddate = new SimpleDateFormat("yyyy년 MM월 dd일");
        Date time = new Date();
        String today_date = coviddate.format(time);

        frag_covid_date.setText(today_date);

        String todaydecide = mainAct.coviditem.get(0);
        String todayexam = mainAct.coviditem.get(1);
        String todayclear = mainAct.coviditem.get(2);
        String todaydeath = mainAct.coviditem.get(3);

        String yestdecide = mainAct.coviditem.get(4);
        String yestexam = mainAct.coviditem.get(5);
        String yestclear = mainAct.coviditem.get(6);
        String yestdeath = mainAct.coviditem.get(7);

        int todecide = Integer.parseInt(todaydecide);
        int toexam = Integer.parseInt(todayexam);
        int toclear = Integer.parseInt(todayclear);
        int todeath = Integer.parseInt(todaydeath);

        int ydecide = Integer.parseInt(yestdecide);
        int yexam = Integer.parseInt(yestexam);
        int yclear = Integer.parseInt(yestclear);
        int ydeath = Integer.parseInt(yestdeath);

        int result_decide = todecide-ydecide;
        int result_exam = toexam-yexam;
        int result_clear = toclear-yclear;
        int result_death = todeath-ydeath;

        frag_covid_decideCnt.setText(todaydecide+" 명");
        frag_covid_examCnt.setText(todayexam+" 명");
        frag_covid_clearCnt.setText(todayclear+" 명");
        frag_covid_deathCnt.setText(todaydeath+" 명");

        if(result_decide>0){
            String decide = Integer.toString(Math.abs(result_decide));
            frag_covid_decideupdown.setText(decide+" ▲");
            frag_covid_todaydecide.setText("일일 확진자 : "+decide + " 명");
        }
        else if(result_decide<0){
            String decide = Integer.toString(Math.abs(result_decide));
            frag_covid_decideupdown.setText(decide+" ▼");
            frag_covid_todaydecide.setText("-");
        }
        else if(result_decide==0){
            frag_covid_decideupdown.setText("-");
            frag_covid_todaydecide.setText("-");
        }

        if(result_exam>0){
            String exam = Integer.toString(Math.abs(result_exam));
            frag_covid_examupdown.setText(exam+" ▲");
        }
        else if(result_exam<0){
            String exam = Integer.toString(Math.abs(result_exam));
            frag_covid_examupdown.setText(exam+" ▼");
        }
        else if(result_exam==0){
            frag_covid_examupdown.setText("-");
        }

        if(result_clear>0){
            String clear = Integer.toString(Math.abs(result_clear));
            frag_covid_clearupdown.setText(clear+" ▲");
        }
        else if(result_clear<0){
            String clear = Integer.toString(Math.abs(result_clear));
            frag_covid_clearupdown.setText(clear+" ▼");
        }
        else if(result_clear==0){
            frag_covid_clearupdown.setText("-");
        }

        if(result_death>0){
            String death = Integer.toString(Math.abs(result_death));
            frag_covid_deathupdown.setText(death+" ▲");
        }
        else if(result_death<0){
            String death = Integer.toString(Math.abs(result_death));
            frag_covid_deathupdown.setText(death+" ▼");
        }
        else if(result_death==0){
            frag_covid_deathupdown.setText("-");
        }


        
        return view;
    }
}
