package com.example.weathertracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weathertracker.dialogs.InitialDialogFragment

const val isInitializedTag = "INIT_STATE"
private const val TAG = "SplashScreen"
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE_GPS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                || (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
               /* editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,true)
                editor.commit()*/
                Log.i(TAG, "onRequestPermissionsResult: ")
            } else {
                /*editor.putBoolean(Constants.PERMISSIONS_IS_ENABLED,false)
                editor.commit()*/
            }
        }
    }

     private fun showCustomDialog(){
        supportFragmentManager
            .beginTransaction()
            .add(InitialDialogFragment(),"initialDialog")
            .commit()
    }
}