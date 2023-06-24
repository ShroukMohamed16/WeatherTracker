package com.example.weathertracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({

            val intent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(this@SplashScreen, "Welcome", Toast.LENGTH_SHORT).show()

        }, 5000)
    }
}