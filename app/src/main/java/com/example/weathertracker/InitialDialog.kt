package com.example.weathertracker

import android.app.Dialog
import android.content.Context
import android.widget.Button

class InitialDialog(context:Context):Dialog(context) {
    init {
        setContentView(R.layout.intial_dialog)
        val button = findViewById<Button>(R.id.ok_btn_initial_dialog)
        button.setOnClickListener{
            dismiss()
        }

    }
}