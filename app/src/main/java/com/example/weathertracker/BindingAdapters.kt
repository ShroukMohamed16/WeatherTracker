package com.example.weathertracker

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("myImg", "myError")
fun loadImg(view: ImageView, url: String?, error: Drawable) {
    Glide.with(view.context)
        .load(" https://openweathermap.org/img/wn/$url@2x.png")
        .error(error)
        .into(view)
}
