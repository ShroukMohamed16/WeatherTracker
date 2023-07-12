package com.example.weathertracker.alarm.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.weathertracker.Constants
import com.example.weathertracker.R
import com.example.weathertracker.alarm.myWorker
import com.example.weathertracker.alarm.viewmodel.AlarmViewModel
import com.example.weathertracker.alarm.viewmodel.AlarmViewModelFactory
import com.example.weathertracker.databinding.FragmentAlertDialogBinding
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.favorite.HomeRoomState
import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "AlertDialogFragment"
class AlertDialogFragment : DialogFragment(),DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    lateinit var binding: FragmentAlertDialogBinding
    var type:String = ""

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    var endDateInMillis:Long? = null
    var startDateInMillis:Long? =null
    var endTimeInMillis:Long? = null
    var startTimeInMillis:Long? = null

    private lateinit var calender:Calendar
    lateinit var alarmViewModel: AlarmViewModel
    lateinit var alarmViewModelFactory: AlarmViewModelFactory
    var alarmOrNotification :String = "alarm"
    lateinit var desc:String
    lateinit var icon:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmViewModelFactory = AlarmViewModelFactory(Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(requireContext())))
        alarmViewModel = ViewModelProvider(this,alarmViewModelFactory).get(AlarmViewModel::class.java)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_alert_dialog, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.to.setOnClickListener {
            type = "end"
            getDateTimeCalender()

            val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
            datePickerDialog.datePicker.minDate = calender.timeInMillis
            datePickerDialog.show()
        }
        binding.from.setOnClickListener {
            type = "start"
            getDateTimeCalender()

            val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
            datePickerDialog.datePicker.minDate = calender.timeInMillis
            datePickerDialog.show()
        }
        binding.cancelAlertDialog.setOnClickListener {
            dismiss()
        }
        binding.addAlertDialog.setOnClickListener {

            if (startDateInMillis != null && endDateInMillis != null && startTimeInMillis != null && endTimeInMillis != null) {
                if (Settings.canDrawOverlays(requireContext())) {
                    Log.i(TAG, "onViewCreated: overLayers Permission on")
                    if (checkAlertDurationValidation()) {
                        Log.i(TAG, "onViewCreated: Validate Date")

                        val alarm = Alarm(
                            startTimeInMillis!!,
                            endTimeInMillis!!,
                            startDateInMillis!!,
                            endDateInMillis!!
                        )

                        checkAlarmOrNotification()
                        Log.i(TAG, "onViewCreated: Alarm or Notification Checked")
                        lifecycleScope.launch {
                            alarmViewModel.getWeatherAlarms()
                            Log.i(TAG, "onViewCreated: call getWeatherFromRoom")
                            alarmViewModel.weatherAlarmsList.collectLatest { result ->
                                when (result) {
                                    is HomeRoomState.Success -> {
                                        Log.i(TAG, "onViewCreated: Successs")
                                        if (!result.data.alerts.isNullOrEmpty()) {
                                            Log.i(TAG, "onViewCreated: null")
                                            desc = result.data.alerts!!.get(0).description
                                        } else {
                                            Log.i(TAG, "onViewCreated: not null")
                                            desc = getString(R.string.beautiful_weather)
                                        }
                                        icon = R.drawable.sky.toString()
                                        alarmViewModel.insertAlarm(alarm)
                                        val requestData = Data.Builder()
                                            .putString("alarm", Gson().toJson(alarm))
                                            .putString("desc", desc)
                                            .putString("icon", icon)
                                            .putString("AlarmOrNotification", alarmOrNotification)
                                            .putLong("startTimeOfAlert", startTimeInMillis!!)
                                            .build()

                                        val dateFormat =
                                            SimpleDateFormat("HH:mm a") 

                                        val fullDate = dateFormat.format(startTimeInMillis)
                                        val currentTime = dateFormat.format(System.currentTimeMillis())

                                        Log.i(TAG, "onViewCreated: $fullDate $currentTime")
                                        Log.i(TAG, "onViewCreated: ${startTimeInMillis!! - System.currentTimeMillis()}")
                                        Log.i(TAG, "onViewCreated: ${startTimeInMillis!! - Calendar.getInstance().timeInMillis}")
                                        val request = OneTimeWorkRequestBuilder<myWorker>()
                                            .setInitialDelay(
                                                startTimeInMillis!! - System.currentTimeMillis(),
                                                TimeUnit.MILLISECONDS
                                            )
                                            .setInputData(requestData)
                                            .setConstraints(
                                                Constraints.Builder()
                                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                                    .build()
                                            )
                                            .addTag(alarm.startDate.toString())
                                            .build()

                                        WorkManager
                                            .getInstance(requireContext())
                                            .enqueue(request)
                                        Log.i(TAG, "onViewCreated: requestCreated")

                                    }
                                    else -> {
                                        Toast.makeText(
                                            requireContext(),
                                            "",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }
                        }

                    }
                }else {
                        checkDisplayOverlayerPermission()
                }
            }else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Please fill all data")
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
    private fun getDateTimeCalender(){
        calender = Calendar.getInstance()
        year =  calender.get(Calendar.YEAR)
        month = calender.get(Calendar.MONTH)
        day = calender.get(Calendar.DAY_OF_MONTH)
        hour = calender.get(Calendar.HOUR)
        minute =  calender.get(Calendar.MINUTE)

    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.DRAW_OVER_OTHER_APPS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(view!!,"Generated",Snackbar.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(),"Not Generated",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun convertDate(calender: Calendar) {
        val formatDate = " EEE,d MMM"
        val sdf = SimpleDateFormat(formatDate, Locale.getDefault())
        val formatTime = " hh : mm a"
        val stf = SimpleDateFormat(formatTime, Locale.getDefault())
        if(type == "end") {
            binding.endDay.text = sdf.format(Date(savedYear,savedMonth,savedDay))
            binding.endTime.text = stf.format(Time(savedHour,savedMinute,0))


            endDateInMillis = dateToMillis(savedDay,savedMonth,savedYear)
             endTimeInMillis = timeToMillis(savedHour,savedMinute)
            Log.i(TAG, "dateInMillis:$endDateInMillis TimeInMillis:$endTimeInMillis ")
        }
        else if(type == "start") {
            binding.startDay.text = sdf.format(Date(savedYear,savedMonth,savedDay))
            binding.startTime.text = stf.format(Time(savedHour,savedMinute,0))
            startDateInMillis = dateToMillis(savedDay,savedMonth,savedYear)
            startTimeInMillis = timeToMillis(savedHour,savedMinute)
            Log.i(TAG, "dateInMillis:$startDateInMillis TimeInMillis:$startTimeInMillis ")

        }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        Log.i(TAG, "onDateSet: day $dayOfMonth month $month year $year")
        calender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        calender.set(Calendar.MONTH,month)
        calender.set(Calendar.YEAR,year)

        getDateTimeCalender()
        TimePickerDialog(requireContext(),this,hour,minute,false).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        Log.i(TAG, "onTimeSet: Hour $hourOfDay Minute $minute")

        calender.set(Calendar.HOUR,hourOfDay)
        calender.set(Calendar.MINUTE,minute)
        convertDate(calender)
    }
    private fun dateToMillis(day: Int, month: Int, year: Int): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(year, month , day) // Note: months in Calendar are 0-indexed
        return calendar.timeInMillis
    }

    private fun timeToMillis(hourOfDay: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
       // return calendar.timeInMillis % (24 * 60 * 60 * 1000) // Modulo to get milliseconds since midnight
    }
    private fun checkDisplayOverlayerPermission(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Allow to Display on Other Apps")
        builder.setPositiveButton(R.string.ok) { dialog, it ->
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
            )
            startActivityForResult(
                intent,
                Constants.DRAW_OVER_OTHER_APPS_REQUEST_CODE
            )
        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun checkAlertDurationValidation(): Boolean {

            if (startDateInMillis!! <= endDateInMillis!!) {
                if (startTimeInMillis!! <= endTimeInMillis!!) {
                    Toast.makeText(
                        requireContext(),
                        "True",
                        Toast.LENGTH_LONG
                    ).show()
                    return true
                } else {
                    Toast.makeText(
                        requireContext(),
                        "End time should be after start time",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "End date should be after start date",
                    Toast.LENGTH_LONG
                ).show()
            }
        return false

    }
    private fun checkAlarmOrNotification(){
       if(binding.alarmRadioBtn.isChecked)
            alarmOrNotification = "alarm"
       else if(binding.notificationRadioBtn.isChecked)
            alarmOrNotification = "notification"
    }


}