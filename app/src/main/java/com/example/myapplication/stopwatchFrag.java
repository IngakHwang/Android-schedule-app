package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class stopwatchFrag extends Fragment{

    private TextView stopwatch_timeView,stopwatch_recordView;
    private Button stopwatch_startbtn, stopwatch_recordbtn, stopwatch_pausebtn, stopwatch_stopbtn;
    private Thread timeThread = null;
    private Boolean isRunning = true;

    public stopwatchFrag(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.stopwatch, container, false);
        stopwatch_startbtn = (Button)v.findViewById(R.id.stopwatch_startbtn);
        stopwatch_recordbtn = (Button)v.findViewById(R.id.stopwatch_recordbtn);
        stopwatch_pausebtn = (Button)v.findViewById(R.id.stopwatch_pausebtn);
        stopwatch_stopbtn = (Button)v.findViewById(R.id.stopwatch_stopbtn);
        stopwatch_timeView = (TextView)v.findViewById(R.id.stopwatch_timeView);
        stopwatch_recordView = (TextView)v.findViewById(R.id.stopwatch_recordView);

        // 시작버튼 실행 후에 시작버튼은 사라지고,
        // 기록, 일시정지, 정지 버튼 생성 후에 스톱워치 쓰레드 시작
            // 스톱워치 쓰레드
            // msg에 int i(0)가 담겨지고 0.01초에 한번씩 1씩 더해진다
            // msg를 handler로 보내고 handler에서 msg에 담긴 int i 값을 받아 각각 mSec, sec, min, hour 변수에 값을 담는다
            // 담긴 값을 String result에 00:00:00:00 값으로 담고 stopwatch_textView에 setText 한다

        stopwatch_startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                stopwatch_recordbtn.setVisibility(View.VISIBLE);
                stopwatch_pausebtn.setVisibility(View.VISIBLE);
                stopwatch_stopbtn.setVisibility(View.VISIBLE);

                timeThread = new Thread(new timeThread());
                timeThread.start();
            }
        });
        stopwatch_stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                stopwatch_startbtn.setVisibility(View.VISIBLE);

                stopwatch_recordbtn.setVisibility(View.GONE);
                stopwatch_pausebtn.setVisibility(View.GONE);

                timeThread.interrupt();

                stopwatch_recordView.setText("");
                stopwatch_timeView.setText("");
                stopwatch_timeView.setText("00:00:00:00");

            }
        });
        stopwatch_pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = !isRunning;
                if(isRunning){
                    stopwatch_pausebtn.setText("일시정지");
                } else{
                    stopwatch_pausebtn.setText("시작");
                }
            }
        });
        stopwatch_recordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopwatch_recordView.setText(stopwatch_recordView.getText() + stopwatch_timeView.getText().toString() + "\n");
            }
        });

        return v;
        //return inflater.inflate(R.layout.stopwatch, container, false);
    }

    // msg에 int i(0)가 담겨지고 0.01초에 한번씩 1씩 더해진다
    // msg를 handler로 보내고 handler에서 msg에 담긴 int i 값을 받아 각각 mSec, sec, min, hour 변수에 값을 담는다
    // 담긴 값을 String result에 00:00:00:00 값으로 담고 stopwatch_textView에 setText 한다


    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            int mSec = msg.arg1 % 100;
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            int hour = (msg.arg1 / 100) / 360;

            String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
            stopwatch_timeView.setText(result);
        }
    };
    public class timeThread implements Runnable{
        @Override
        public void run() {
            int i = 0;
            while (true){
                while(isRunning){
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try{
                        Thread.sleep(10);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        return;
                    }

                }
            }
        }
    }

}
