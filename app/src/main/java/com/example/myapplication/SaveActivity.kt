package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager

class SaveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_save)

        supportActionBar?.hide()

        Handler().postDelayed({
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        },500)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}