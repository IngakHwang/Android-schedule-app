package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class timerAct extends AppCompatActivity {

    Button timer_stopwatch, timer_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setTitle("스톱워치 / 타이머");

        timer_stopwatch = (Button) findViewById(R.id.timer_stopwatch);
        timer_timer = (Button) findViewById(R.id.timer_timer);


        // 스톱워치 프래그먼트 생성
        timer_stopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                stopwatchFrag stopwatchFrag = new stopwatchFrag();
                transaction.replace(R.id.timer_frame,stopwatchFrag);
                transaction.commit();
            }
        });

        // 타이머 프래그먼트 생성
        timer_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                timerFrag timerFrag = new timerFrag();
                transaction.replace(R.id.timer_frame,timerFrag);
                transaction.commit();
            }
        });


    }
}