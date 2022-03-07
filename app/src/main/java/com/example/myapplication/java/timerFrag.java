package com.example.myapplication.java;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.util.Timer;
import java.util.TimerTask;

public class timerFrag extends Fragment {

    private EditText timer_minuteIP, timer_secondIP;

    private Button timer_startbtn, timer_stopbtn, timer_cancelbtn;

    private TextView timer_view;

    private CountDownTimer countDownTimer;

    private boolean timerRunning;
    private boolean firstState;

    private long time, tempTime;

    FrameLayout timer_settinglayout, timer_startlayout;

    MediaPlayer sound;


    public timerFrag(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timer, container, false);

        timer_view = (TextView) v.findViewById(R.id.timer_view); // 타이머 현황

        timer_minuteIP = (EditText) v.findViewById(R.id.timer_minuteIP);// 분 입력값
        timer_secondIP = (EditText) v.findViewById(R.id.timer_secondIP);// 초 입력값

        timer_startbtn = (Button) v.findViewById(R.id.timer_startbtn);  // 시작버튼
        timer_stopbtn = (Button) v.findViewById(R.id.timer_stopbtn);    // 일시정지버튼
        timer_cancelbtn = (Button) v.findViewById(R.id.timer_cancelbtn);// 취소버튼

        timer_settinglayout = (FrameLayout) v.findViewById(R.id.timer_settinglayout);
        timer_startlayout = (FrameLayout) v.findViewById(R.id.timer_startlayout);

        sound = MediaPlayer.create(getActivity(),R.raw.timerdone);

        timer_startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstState = true;

                timer_settinglayout.setVisibility(timer_settinglayout.GONE);
                timer_startlayout.setVisibility(timer_startlayout.VISIBLE);

                startStop();

            }
        });

        timer_stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });

        timer_cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer_settinglayout.setVisibility(timer_settinglayout.VISIBLE);
                timer_startlayout.setVisibility(timer_startlayout.GONE);
                firstState=true;
                stopTimer();
            }
        });
        updateTimer();

        return v;
    }

    private void startStop(){
        if(timerRunning){
            stopTimer();
        }
        else{
            startTimer();
        }
    }

    private void startTimer(){

        if(firstState){
            String sMin = timer_minuteIP.getText().toString();
            String sSecond = timer_secondIP.getText().toString();

            time = (Long.parseLong(sMin)*60000) + (Long.parseLong(sSecond) * 1000) + 1000;
        }
        else{
            time = tempTime;
        }
        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tempTime = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();

        timer_stopbtn.setText("일시정지");
        timerRunning = true;
        firstState = false;
    }

    private void stopTimer(){
        countDownTimer.cancel();
        timerRunning=false;
        timer_stopbtn.setText("계속");
    }

    private void updateTimer(){

        int minutes = (int) tempTime % 3600000 / 60000;
        int seconds = (int) tempTime % 60000 / 1000;


        String timeLeftText = "";
        if(minutes < 10) timeLeftText += "0";
        timeLeftText += minutes + ":";

        if (seconds <10 ) timeLeftText +="0";
        timeLeftText += seconds;

        timer_view.setText(timeLeftText);

        if(timeLeftText.equals("00:00") && timerRunning==true){
            timer_settinglayout.setVisibility(timer_settinglayout.VISIBLE);
            timer_startlayout.setVisibility(timer_startlayout.GONE);
            firstState=true;
            stopTimer();
            sound.start();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("타이머").setMessage("타이머가 종료되었습니다.");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }
}
