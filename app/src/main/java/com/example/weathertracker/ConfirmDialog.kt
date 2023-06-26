package com.example.weathertracker

import android.app.Dialog
import android.content.Context
import com.google.android.material.button.MaterialButton

class ConfirmDialog(context: Context): Dialog(context) {
    init {
        setContentView(R.layout.confirm_dialog)
        val button = findViewById<MaterialButton>(R.id.save_btn_confirm_dialog)
        button.setOnClickListener{
            dismiss()
        }

    }
}