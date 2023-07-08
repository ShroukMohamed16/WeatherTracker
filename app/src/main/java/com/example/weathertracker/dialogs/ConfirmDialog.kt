package com.example.weathertracker.dialogs

import android.app.Dialog
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.weathertracker.MainActivity
import com.example.weathertracker.R
import com.google.android.material.button.MaterialButton

class ConfirmDialog(location:String):DialogFragment(){

    private lateinit var saveButton: MaterialButton
    lateinit var location:TextView
    //lateinit var address: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.confirm_dialog, null)
        saveButton = view.findViewById(R.id.save_btn_confirm_dialog)
        location = view.findViewById(R.id.location_confirm_dialog)
        //address = view.findViewById(R.id.address_confirm_dialog)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
            .setTitle("Custom Dialog")
        val dialog = builder.create()

        saveButton.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return dialog
    }
}
