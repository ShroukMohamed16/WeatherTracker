package com.example.weathertracker

import android.content.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(Constants.isInitializedTag, true).apply()

        val  actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu_icon)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        val navController =  Navigation.findNavController(this,R.id.nav_host)
        NavigationUI.setupWithNavController(navigationView , navController)



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initUI() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
    }

}