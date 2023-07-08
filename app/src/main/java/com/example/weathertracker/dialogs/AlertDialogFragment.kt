package com.example.weathertracker.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.weathertracker.R
import com.example.weathertracker.databinding.FragmentAlertDialogBinding
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*


class AlertDialogFragment : DialogFragment(),DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    lateinit var binding: FragmentAlertDialogBinding
    var type:String = ""

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0
    lateinit var calender:Calendar

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
        }
        else if(type == "start") {
            binding.startDay.text = sdf.format(Date(savedYear,savedMonth,savedDay))
            binding.startTime.text = stf.format(Time(savedHour,savedMinute,0))
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        calender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        calender.set(Calendar.MONTH,month)
        calender.set(Calendar.YEAR,year)


        getDateTimeCalender()
        TimePickerDialog(requireContext(),this,hour,minute,false).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        calender.set(Calendar.HOUR,hourOfDay)
        calender.set(Calendar.MINUTE,minute)
        convertDate(calender)
    }


}