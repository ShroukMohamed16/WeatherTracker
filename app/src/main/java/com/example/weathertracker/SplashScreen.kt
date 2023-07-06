package com.example.weathertracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.weathertracker.dialogs.InitialDialogFragment

const val isInitializedTag = "INIT_STATE"

class SplashScreen : AppCompatActivity() {

   lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        var isInitialized:Boolean =sharedPreferences.getBoolean(isInitializedTag,false)

        Handler().postDelayed({
            if(!isInitialized) {
                showCustomDialog()
            }else{
                val intent = Intent(this@SplashScreen,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 5000)


    }



     private fun showCustomDialog(){
        supportFragmentManager
            .beginTransaction()
            .add(InitialDialogFragment(),"initialDialog")
            .commit()
    }
}