package com.example.weathertracker.favorite.view

import com.example.weathertracker.model.FavoriteItem

interface OnClickListener {
    fun onClickFavItem(favoriteItem: FavoriteItem)
    fun onClickDeleteItem(favoriteItem: FavoriteItem)
}