package com.example.myapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.example.myapplication.databinding.TimerBinding

class TimerFragment : Fragment() {
    //lateinit var binding: TimerBinding

    var firstState = true
    var timerRunning = false

    var time: Long = 0
    var tempTime: Long = 0

    lateinit var timerView : TextView
    lateinit var timerMinute : EditText
    lateinit var timerSecond : EditText
    lateinit var timerStartBTN : Button
    lateinit var timerStopBTN : Button
    lateinit var timerCancelBTN : Button
    lateinit var timerSettingLayout : FrameLayout
    lateinit var timerStartLayout : FrameLayout
    lateinit var sound : MediaPlayer
    lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.timer, container, false)

        timerView = v.findViewById<TextView>(R.id.timer_view)

        timerMinute = v.findViewById<EditText>(R.id.timer_minuteIP)
        timerSecond = v.findViewById<EditText>(R.id.timer_secondIP)

        timerStartBTN = v.findViewById<Button>(R.id.timer_startbtn)
        timerStopBTN = v.findViewById<Button>(R.id.timer_stopbtn)
        timerCancelBTN = v.findViewById<Button>(R.id.timer_cancelbtn)

        timerSettingLayout = v.findViewById<FrameLayout>(R.id.timer_settinglayout)
        timerStartLayout = v.findViewById<FrameLayout>(R.id.timer_startlayout)

        sound = MediaPlayer.create(activity,R.raw.timerdone)

        timerStartBTN.setOnClickListener {
            firstState = true

            timerSettingLayout.visibility = View.GONE
            timerStartLayout.visibility = View.VISIBLE

            startStop()

        }

        timerStopBTN.setOnClickListener {
            startStop()
        }

        timerCancelBTN.setOnClickListener {
            timerSettingLayout.visibility = View.VISIBLE
            timerStartLayout.visibility = View.GONE
            firstState=true
            stopTimer()
        }
        updateTimer()

        return v

    }

    private fun startStop(){
        if(timerRunning) {stopTimer()}
        else {startTimer()}
    }

    private fun startTimer(){

        time = when{
            firstState -> {
                val min = timerMinute.text.toString()
                val second = timerSecond.text.toString()
                min.toLong() * 60000 + second.toLong() * 1000 + 1000
            } else ->{
                tempTime
            }
        }

        countDownTimer = object : CountDownTimer(time, 1000){
            override fun onTick(p0: Long) {
                tempTime = p0
                updateTimer()
            }

            override fun onFinish() {}
        }.start()

        timerStopBTN.text = "일시정지"
        timerRunning = true
        firstState = false
    }

    private fun stopTimer(){
        countDownTimer.cancel()
        timerRunning = false
        timerStopBTN.text = "계속"
    }

    private fun updateTimer() {
        val minute : Int = (tempTime % 3600000 / 60000).toInt()
        val second : Int = (tempTime % 60000 / 1000).toInt()

        var timeLeftText = ""
        if(minute < 10) timeLeftText += "0"
        timeLeftText += "$minute:"

        if(second < 10) timeLeftText += "0"
        timeLeftText += second

        timerView.text=timeLeftText

        if(timeLeftText == "00:00" && timerRunning){
            timerSettingLayout.visibility = View.VISIBLE
            timerStartLayout.visibility = View.GONE
            firstState = true
            stopTimer()
            sound.start()

            val builder = AlertDialog.Builder(context)

            builder.setTitle("타이머").setMessage("타이머가 종료되었습니다.")
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->

            })

            builder.create().show()
        }
    }
}
