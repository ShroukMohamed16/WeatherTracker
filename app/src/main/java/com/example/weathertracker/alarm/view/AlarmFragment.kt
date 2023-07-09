package com.example.weathertracker.alarm.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weathertracker.R
import com.example.weathertracker.alarm.AlarmRoomState
import com.example.weathertracker.databinding.FragmentAlarmBinding
import com.example.weathertracker.alarm.view.AlertDialogFragment
import com.example.weathertracker.alarm.viewmodel.AlarmViewModel
import com.example.weathertracker.alarm.viewmodel.AlarmViewModelFactory
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.model.Alarm
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "AlarmFragment"
class AlarmFragment : Fragment() {

    lateinit var alarmViewModel: AlarmViewModel
    lateinit var alarmViewModelFactory: AlarmViewModelFactory
    lateinit var binding:FragmentAlarmBinding
    lateinit var myAlarmAdapter: AlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmViewModelFactory = AlarmViewModelFactory(Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(requireContext())))
        alarmViewModel = ViewModelProvider(this,alarmViewModelFactory).get(AlarmViewModel::class.java)
        alarmViewModel.getAllAlarms()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_alarm, container, false)
        binding.lifecycleOwner = this
        myAlarmAdapter = AlarmAdapter { alarm: Alarm ->
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure for delete this item ?")
            builder.setPositiveButton("Ok") { dialog, it ->
                alarmViewModel.deleteAlarm(alarm)
                Toast.makeText(requireContext(), "item deleted", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            alarmViewModel.alarmsList.collectLatest { result ->
                when(result){
                    is AlarmRoomState.Success ->{
                        binding.alarmProgressBar.visibility = View.GONE
                        binding.alarmRv.visibility = View.VISIBLE
                        if(result.data.size > 0) {
                            binding.alertImg.visibility = View.GONE
                            binding.alerTxt.visibility = View.GONE
                        }else{
                            binding.alertImg.visibility = View.VISIBLE
                            binding.alerTxt.visibility = View.VISIBLE
                        }
                        Log.i(TAG, "onViewCreated: ${result.data.size}")
                        binding.alarmAdapter = myAlarmAdapter
                        myAlarmAdapter.submitList(result.data)


                    }
                    is AlarmRoomState.Loading->{
                        binding.alarmProgressBar.visibility = View.VISIBLE
                        binding.alarmRv.visibility = View.GONE
                    }
                    else->{
                        Toast.makeText(requireContext(),"Error $result", Toast.LENGTH_LONG)
                    }

                }
            }
        }

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