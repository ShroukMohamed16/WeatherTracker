package com.example.weathertracker.alarm.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.weathertracker.R
import com.example.weathertracker.alarm.viewmodel.AlarmViewModel
import com.example.weathertracker.alarm.viewmodel.AlarmViewModelFactory
import com.example.weathertracker.databinding.FragmentAlertDialogBinding
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.to.setOnClickListener {
            type = "end"
            getDateTimeCalender()

            val datePickerDialog = DatePickerDialog(requireContext(),this,year,month,day)
            datePickerDialog.datePicker.minDate=calender.timeInMillis
            datePickerDialog.show()
        }
        binding.from.setOnClickListener {
            type = "start"
            getDateTimeCalender()

            val datePickerDialog = DatePickerDialog(requireContext(),this,year,month,day)
                datePickerDialog.datePicker.minDate=calender.timeInMillis
                datePickerDialog.show()
        }
        binding.addAlertDialog.setOnClickListener {
            if(startDateInMillis != null && endDateInMillis!=null&& startTimeInMillis!=null && endTimeInMillis!=null){
                alarmViewModel.insertAlarm(Alarm(startTimeInMillis!!,
                    endTimeInMillis!!, startDateInMillis!!, endDateInMillis!!
                ))
                dismiss()
            }else{
                Toast.makeText(requireContext(),"Please enter all Dates and Times",Toast.LENGTH_LONG)
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
        calendar.set(year, month - 1, day) // Note: months in Calendar are 0-indexed
        return calendar.timeInMillis
    }

    private fun timeToMillis(hourOfDay: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis % (24 * 60 * 60 * 1000) // Modulo to get milliseconds since midnight
    }



}