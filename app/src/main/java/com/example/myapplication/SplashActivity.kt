package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Kotlin","Splash")
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        setContentView(R.layout.activity_splash)

//        val actionBar = supportActionBar
//        actionBar?.hide()

        actionBar
        supportActionBar?.hide()

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(applicationContext,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },3000)

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}