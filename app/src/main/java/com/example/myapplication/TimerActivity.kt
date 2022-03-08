package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {
    lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "스톱워치 / 타이머"

        binding.timerStopwatch.setOnClickListener {
            val stopWatchFrag = StopWatchFragment()
            supportFragmentManager.beginTransaction().replace(R.id.timer_frame,stopWatchFrag).commit()
        }

        binding.timerTimer.setOnClickListener {
            val timerFrag = TimerFragment()
            supportFragmentManager.beginTransaction().replace(R.id.timer_frame,timerFrag).commit()
        }
    }
}