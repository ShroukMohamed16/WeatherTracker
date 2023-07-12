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
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weathertracker.Constants
import com.example.weathertracker.R
import com.example.weathertracker.alarm.viewmodel.AlarmViewModel
import com.example.weathertracker.alarm.viewmodel.AlarmViewModelFactory
import com.example.weathertracker.databinding.FragmentNotificationBinding
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
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

   val repo = Repository.getInstance(ApiClient.getInstance(), ConcreteLocalSource(applicationContext))


    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        startWorker()

        if(System.currentTimeMillis() > alarm!!.endTime){
            repo.deleteAlarmFromRoom(alarm!!)
            val worker = WorkManager.getInstance(applicationContext)
            worker.cancelAllWorkByTag(alarm!!.startDate.toString())

        }
        repo.deleteAlarmFromRoom(alarm!!)

        return Result.Success()
    }

    suspend fun startWorker() {
        val strAlertGson = inputData.getString("alarm")
        alarm = Gson().fromJson(strAlertGson, Alarm::class.java)

        val current_date = Date(currentTimeMillis)
        val sdf = SimpleDateFormat("HH:mm")
        val formattedTime: String = sdf.format(current_date)

        val start_date = Date(alarm!!.startTime)
        val startFormatted = sdf.format(start_date)

        val end_date = Date(alarm!!.endTime)
        val endFormatted = sdf.format(end_date)


        Log.i(TAG, "startWorker: $formattedTime $startFormatted $endFormatted")
        if (formattedTime >= startFormatted && formattedTime <= endFormatted) {
            val alarmOrNotification = inputData.getString("AlarmOrNotification")
            icon = inputData.getString("icon")!!
            if (alarmOrNotification == "alarm") {
                withContext(Dispatchers.Main) {
                    val context = applicationContext ?: return@withContext Result.failure()
                    val themeContext = ContextThemeWrapper(context, R.style.Theme_WeatherTracker)
                    showAlertDialog(themeContext,inputData.getString("desc")!!)
                }
            } else {
                createNotification()
                repo.localSource.deleteFromAlarms(alarm!!)

            }

        }

    }

  //  @SuppressLint("SuspiciousIndentation")
    @SuppressLint("SuspiciousIndentation")
      fun showAlertDialog(context: Context,desc: String) {
        val alertDialogBinding =
            FragmentNotificationBinding.inflate(LayoutInflater.from(context))
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setView(alertDialogBinding.root)
        alertDialogBinding.weatherDescription.text = desc

        val dialog = alertDialogBuilder.create().apply {
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setGravity(Gravity.TOP)
        }

        // Load and play the sound effect
        val mediaPlayer = MediaPlayer.create(context, R.raw.aletr_sound)
        mediaPlayer.start()
        alertDialogBinding.imageView3.setImageResource(Constants.setIcon(icon))

        alertDialogBinding.dismissBtn.setOnClickListener {
            Log.i(TAG, "showAlertDialog: Dismiss")
            dialog.dismiss()

            mediaPlayer.release()
            val worker = WorkManager.getInstance(context)
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
            .setContentTitle("Weather Today")
            .setContentText(inputData.getString("desc")!!)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(applicationContext,NotificationManager::class.java)
        notificationManager!!.notify(1, builder.build())

    }





}