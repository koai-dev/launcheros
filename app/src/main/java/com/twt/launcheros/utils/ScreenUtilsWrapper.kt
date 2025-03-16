package com.twt.launcheros.utils

import android.content.Context
import com.koai.base.utils.ScreenUtils

class ScreenUtilsWrapper(private val context: Context) {
    fun getScreenWidth() = ScreenUtils.getScreenWidth(context)
    fun getScreenHeight() = ScreenUtils.getScreenHeight(context)
}