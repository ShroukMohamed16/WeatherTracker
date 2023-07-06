package com.example.weathertracker.favorite.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weathertracker.Constants
import com.example.weathertracker.MapActivity
import com.example.weathertracker.R
import com.example.weathertracker.RoomState
import com.example.weathertracker.databinding.FragmentFavoriteBinding
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.favorite.viewmodel.FavoriteViewModel
import com.example.weathertracker.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment() {

    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    lateinit var binding: FragmentFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteViewModelFactory = FavoriteViewModelFactory(Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(requireContext())))
        favoriteViewModel = ViewModelProvider(this,favoriteViewModelFactory).get(FavoriteViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_favorite, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            favoriteViewModel.favPlacesList.collectLatest { result->
                when(result){
                    is RoomState.Success ->{
                        binding.favProgressBar.visibility = View.GONE
                        binding.favRv.visibility = View.VISIBLE
                    }
                    is RoomState.Loading ->{
                        binding.favProgressBar.visibility = View.VISIBLE
                        binding.favRv.visibility = View.GONE
                    }
                    else ->{
                        binding.favProgressBar.visibility = View.VISIBLE
                        Toast.makeText(requireContext(),"Error $result", Toast.LENGTH_LONG)
                    }
                }
            }
        }
        binding.favFloatingBtn.setOnClickListener{
            requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.MAP_DESTINATION,"favorite")
                .commit()

            val intent = Intent(requireActivity(),MapActivity::class.java)
            startActivity(intent)
        }
    }


}