package com.astatus.easysocketlansampleserver.util

import android.databinding.BindingAdapter
import android.widget.ImageView


/**
 * Created by Administrator on 2017/11/11.
 */
class BindingUtil {
    @BindingAdapter("bind:image")
    fun showImageByUrl(imageView: ImageView, resId: Int) {
        imageView.setImageResource(resId)
    }
}