package com.example.weathertracker

import android.app.Dialog
import android.content.Context
import android.widget.Button
import com.google.android.material.button.MaterialButton

class AlertDialog(context: Context): Dialog(context) {
    init {
        setContentView(R.layout.alert_dialog)
        val button = findViewById<MaterialButton>(R.id.add_alert_dialog)
        button.setOnClickListener{
            dismiss()
        }

    }
}