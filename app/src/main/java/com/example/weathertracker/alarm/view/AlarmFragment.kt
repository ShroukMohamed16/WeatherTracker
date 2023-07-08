package com.example.weathertracker.alarm.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.weathertracker.R
import com.example.weathertracker.databinding.FragmentAlarmBinding
import com.example.weathertracker.dialogs.AlertDialogFragment

class AlarmFragment : Fragment() {

    lateinit var binding:FragmentAlarmBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = DataBindingUtil.inflate(inflater,R.layout.fragment_alarm, container, false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addAlarmFloatingBtn.setOnClickListener{
          showAlarmDialog()
        }

    }
   private fun showAlarmDialog(){
        childFragmentManager
            .beginTransaction()
            .add(AlertDialogFragment(),"AlarmDialog")
            .commit()
    }


}