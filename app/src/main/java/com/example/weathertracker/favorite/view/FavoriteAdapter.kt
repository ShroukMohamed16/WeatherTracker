package com.example.weathertracker.favorite.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathertracker.R
import com.example.weathertracker.databinding.FavItemBinding
import com.example.weathertracker.databinding.FragmentFavoriteBinding
import com.example.weathertracker.model.FavoriteItem

class FavoriteAdapter(private var onClickListener: OnClickListener):ListAdapter<FavoriteItem, FavoriteAdapter.FavoriteViewHolder>(FavoriteDiffUtil()) {

    lateinit var binding:FavItemBinding
    class FavoriteViewHolder(binding:FavItemBinding):RecyclerView.ViewHolder(binding.root) {}




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.fav_item,parent,false)

        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        var currentObject = getItem(position)
        binding.favItem = currentObject
        binding.favItemConstraint.setOnClickListener {
            onClickListener.onClickFavItem(currentObject)
        }
        binding.deleteIcon.setOnClickListener {
            onClickListener.onClickDeleteItem(currentObject)
        }

    }

}