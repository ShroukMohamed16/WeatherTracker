package com.example.weathertracker.favorite.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.weathertracker.Constants
import com.example.weathertracker.R
import com.example.weathertracker.RoomState
import com.example.weathertracker.databinding.FragmentFavoriteBinding
import com.example.weathertracker.db.ConcreteLocalSource
import com.example.weathertracker.favorite.viewmodel.FavoriteViewModel
import com.example.weathertracker.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weathertracker.map.view.MapActivity
import com.example.weathertracker.model.FavoriteItem
import com.example.weathertracker.model.Repository
import com.example.weathertracker.network.ApiClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment(),OnClickListener {

    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    lateinit var binding: FragmentFavoriteBinding
    lateinit var myFavoriteAdapter: FavoriteAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor:SharedPreferences.Editor
    lateinit var snackbar:Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteViewModelFactory = FavoriteViewModelFactory(Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(requireContext())))
        favoriteViewModel = ViewModelProvider(this,favoriteViewModelFactory).get(FavoriteViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        sharedPreferences.getString(Constants.HOME_DESTINATIO,"initial")
        editor = sharedPreferences.edit()
        favoriteViewModel.getAllFavPlaces()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_favorite, container, false)
        binding.lifecycleOwner = this
        myFavoriteAdapter= FavoriteAdapter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG)

        lifecycleScope.launch {
            favoriteViewModel.favPlacesList.collectLatest { result->
                when(result){
                    is RoomState.Success ->{
                        binding.favProgressBar.visibility = View.GONE
                        binding.favRv.visibility = View.VISIBLE
                        if(result.data.size > 0) {
                            binding.favoriteImg.visibility = View.GONE
                            binding.noPlacesTxt.visibility = View.GONE
                        }
                        binding.favAdapter = myFavoriteAdapter
                        myFavoriteAdapter.submitList(result.data)

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

            val intent = Intent(requireActivity(), MapActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClickFavItem(favoriteItem: FavoriteItem) {
      if(isNetworkAvailable()) {
          editor.putString(Constants.HOME_DESTINATIO, "favorite").commit()
          editor.putString(Constants.FAV_Lat_KEY, favoriteItem.lat.toString())
          editor.putString(Constants.FAV_Lon_Key, favoriteItem.lon.toString())
          editor.putString(Constants.FAV_LOCATION_NAME, favoriteItem.cityName)
          editor.commit()
          val navController = findNavController()
          navController.navigate(R.id.action_favoriteFragment_to_homeFragment)
      }else{
          snackbar.setAction("Dismiss") {
              snackbar.dismiss()
          }
          snackbar.show()
      }
    }

    override fun onClickDeleteItem(favoriteItem: FavoriteItem) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure for delete this item ?")
        builder.setPositiveButton("Ok") { dialog, it ->
            favoriteViewModel.delete(favoriteItem)
            Toast.makeText(requireContext(), "item deleted", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }




}