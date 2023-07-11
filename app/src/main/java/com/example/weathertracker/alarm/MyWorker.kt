package com.example.weathertracker.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weathertracker.Constants
import com.example.weathertracker.R
import com.example.weathertracker.databinding.FragmentNotificationBinding
import com.example.weathertracker.model.Alarm
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MyWorker"
class myWorker(appContext: Context , params:WorkerParameters): CoroutineWorker(appContext,params) {
    private var alarm: Alarm? = null
    private val currentTimeMillis = System.currentTimeMillis()
    lateinit var icon:String

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        startWorker()
        return Result.Success()
    }

    suspend fun startWorker() {
        val strAlertGson = inputData.getString("alarm")
        alarm = Gson().fromJson(strAlertGson, Alarm::class.java)

        val current_date = Date(currentTimeMillis)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val formattedTime: String = sdf.format(current_date)

        val start_date = Date(alarm!!.startDate)
        val startFormatted = sdf.format(start_date)

        val end_date = Date(alarm!!.endDate)
        val endFormatted = sdf.format(end_date)


        Log.i(TAG, "startWorker: $formattedTime $startFormatted ")
        if (formattedTime >= startFormatted && formattedTime <= endFormatted) {
            val alarmOrNotification = inputData.getString("AlarmOrNotification")
            icon = inputData.getString("icon")!!
            if (alarmOrNotification == "alarm") {
                withContext(Dispatchers.Main) {
                    showAlertDialog(inputData.getString("desc")!!)
                }
            } else {
                createNotification()
            }

        }
        else{
            Log.i(TAG, "startWorker: Invaild Time")
        }
    }

  //  @SuppressLint("SuspiciousIndentation")
    @SuppressLint("SuspiciousIndentation")
     fun showAlertDialog(desc: String) {
        val alertDialogBinding =
            FragmentNotificationBinding.inflate(LayoutInflater.from(applicationContext))
        val alertDialogBuilder = AlertDialog.Builder(applicationContext)

        alertDialogBuilder.setView(alertDialogBinding.root)
        alertDialogBinding.weatherDescription.text = desc

        val dialog = alertDialogBuilder.create().apply {
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setGravity(Gravity.TOP)
        }

        // Load and play the sound effect
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.aletr_sound)
        mediaPlayer.start()
        alertDialogBinding.imageView3.setImageResource(Constants.setIcon(icon))

        alertDialogBinding.dismissBtn.setOnClickListener {
            dialog.dismiss()
            mediaPlayer.release()
            val worker = WorkManager.getInstance(applicationContext)
            worker.cancelAllWorkByTag(alarm!!.startDate.toString())
        }
        dialog.show()
    }
    fun createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("my_channel_id", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(applicationContext,NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, "my_channel_id")
            .setSmallIcon(R.drawable.sky)
            .setContentTitle(R.string.weather.toString())
            .setContentText(inputData.getString("desc")!!)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(applicationContext,NotificationManager::class.java)
        notificationManager!!.notify(1, builder.build())
    }





}