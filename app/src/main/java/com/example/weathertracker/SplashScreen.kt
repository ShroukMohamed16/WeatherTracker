package com.example.weathertracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

const val isInitializedTag = "INIT_STATE"
class SplashScreen : AppCompatActivity() {

   lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        var isInitialized:Boolean =sharedPreferences.getBoolean(isInitializedTag,false)

        Handler().postDelayed({
            if(!isInitialized) {
                showCustomDialog()
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.putBoolean(isInitializedTag,true)
                editor.apply()
            }else{
                val intent = Intent(this@SplashScreen,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 5000)

    }



     fun showCustomDialog(){
        supportFragmentManager
            .beginTransaction()
            .add(InitialDialogFragment(),"initialDialog")
            .commit()
    }
}