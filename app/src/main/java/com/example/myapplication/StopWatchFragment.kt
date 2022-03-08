package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class StopWatchFragment : Fragment() {

    lateinit var startBTN : Button
    lateinit var recordBTN : Button
    lateinit var pauseBTN : Button
    lateinit var stopBTN : Button
    lateinit var timeView : TextView
    lateinit var recordView : TextView

    lateinit var timeThread: Thread
    var isRunning = true

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.stopwatch,container,false)

        startBTN = v.findViewById<Button>(R.id.stopwatch_startbtn)
        recordBTN = v.findViewById<Button>(R.id.stopwatch_recordbtn)
        pauseBTN = v.findViewById<Button>(R.id.stopwatch_pausebtn)
        stopBTN = v.findViewById<Button>(R.id.stopwatch_stopbtn)

        timeView = v.findViewById<TextView>(R.id.stopwatch_timeView)
        recordView = v.findViewById<TextView>(R.id.stopwatch_recordView)

        // 시작버튼 실행 후에 시작버튼은 사라지고,
        // 기록, 일시정지, 정지 버튼 생성 후에 스톱워치 쓰레드 시작
        // 스톱워치 쓰레드
        // msg에 int i(0)가 담겨지고 0.01초에 한번씩 1씩 더해진다
        // msg를 handler로 보내고 handler에서 msg에 담긴 int i 값을 받아 각각 mSec, sec, min, hour 변수에 값을 담는다
        // 담긴 값을 String result에 00:00:00:00 값으로 담고 stopwatch_textView에 setText 한다

        startBTN.setOnClickListener {
            startBTN.visibility = View.GONE

            recordBTN.visibility = View.VISIBLE
            pauseBTN.visibility = View.VISIBLE
            stopBTN.visibility = View.VISIBLE

            timeThread = Thread(TimeThread())
            timeThread.start()
        }

        stopBTN.setOnClickListener {
            stopBTN.visibility = View.GONE

            startBTN.visibility = View.VISIBLE

            recordBTN.visibility = View.GONE
            pauseBTN.visibility = View.GONE

            timeThread.interrupt()

            recordView.text = ""
            timeView.text = ""
            timeView.text = "00:00:00:00"
        }

        pauseBTN.setOnClickListener {
            isRunning = !isRunning
            if(isRunning){pauseBTN.text = "일시정지"}
            else {pauseBTN.text = "시작"}
        }

        recordBTN.setOnClickListener {
            recordView.text="${recordView.text} ${timeView.text} \n"
        }

        return v
    }

    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message) {
            val mSec = msg.arg1 % 100
            val sec = (msg.arg1 / 100) % 60
            val min = (msg.arg1 / 100) / 60
            val hour = (msg.arg1 / 100) / 360

            val result : String = String.format("%02d:%02d:%02d:%02d", hour,min,sec,mSec)

            timeView.text = result
        }
    }

    inner class TimeThread : Runnable{
        override fun run() {
            Log.d("스톱워치", "타이머 시작")
            var i = 0
            while(true){
                while(isRunning){
                    val msg = Message()
                    msg.arg1 = i++
                    handler.sendMessage(msg)

                    try{
                        Thread.sleep(10)
                    }catch (e: InterruptedException){
                        Log.d("스톱워치","try 실패")
                        e.printStackTrace()
                        return
                    }

                }
            }
        }
    }

}