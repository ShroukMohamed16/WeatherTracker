package com.example.weathertracker.dialogs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.weathertracker.Constants
import com.example.weathertracker.MainActivity
import com.example.weathertracker.MapActivity
import com.example.weathertracker.R
import com.example.weathertracker.databinding.ConfirmDialogBinding
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
import kotlinx.coroutines.launch
import java.util.*

class ConfirmDialog :DialogFragment(){
}