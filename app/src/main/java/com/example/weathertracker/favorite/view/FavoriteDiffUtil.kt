package com.example.weathertracker.favorite.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weathertracker.model.FavoriteItem

class FavoriteDiffUtil: DiffUtil.ItemCallback<FavoriteItem>() {
    override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
        return oldItem == newItem
    }
}