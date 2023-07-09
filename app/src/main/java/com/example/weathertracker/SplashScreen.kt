package com.example.weathertracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.weathertracker.dialogs.InitialDialogFragment
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity(){

   lateinit var sharedPreferences: SharedPreferences
   private var   isInitialized:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        isInitialized = sharedPreferences.getBoolean(Constants.isInitializedTag,false)
    }

    override fun onResume() {
        super.onResume()
        val language = getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE).getString(Constants.LOCAL_LANGUAGE, "en")
        val locale = language?.let {
            Locale(it)
        }
        resources.configuration.setLocale(locale)
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

     private fun showCustomDialog(){
         Handler(Looper.getMainLooper()).post {
             supportFragmentManager
                 .beginTransaction()
                 .add(InitialDialogFragment(), "initialDialog")
                 .commitAllowingStateLoss()
         }
    }

}