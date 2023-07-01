package com.example.weathertracker

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.button.MaterialButton

private const val TAG = "InitialDialogFragment"
class InitialDialogFragment : DialogFragment() {
    lateinit var gpsRadioButton: RadioButton
    lateinit var mapRadioButton: RadioButton
    lateinit var enableNotificationRadioButton: RadioButton
    lateinit var disableNotificationRadioButton: RadioButton
    lateinit var notificationManager: NotificationManager

    lateinit var okButton: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager =  requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_initial_dialog, container, false)
        gpsRadioButton = view.findViewById(R.id.gps_initial_dialog_radioButton)
        mapRadioButton = view.findViewById(R.id.map_initial_dialog_radioButton)
        enableNotificationRadioButton = view.findViewById(R.id.enable_notification_dialog_radioButton)
        disableNotificationRadioButton = view.findViewById(R.id.disable_notification_initial_dialog_radioButton)
        okButton = view.findViewById(R.id.ok_btn_initial_dialog)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        okButton.setOnClickListener {
            when {
                mapRadioButton.isChecked -> {
                    val intent = Intent(requireContext(), MapActivity::class.java)
                    startActivity(intent)
                    dismiss()
                }

                gpsRadioButton.isChecked -> {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    dismiss()
                }
            }
            when {
                enableNotificationRadioButton.isChecked -> {

                }

                disableNotificationRadioButton.isChecked -> {
                    notificationManager.cancelAll()
                    Log.i(TAG, "onViewCreated: Notification Disabled")
                    dismiss()
                }
            }
        }


    }
}