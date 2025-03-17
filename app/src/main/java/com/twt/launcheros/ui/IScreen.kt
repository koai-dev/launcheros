package com.twt.launcheros.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.koai.base.main.action.router.BaseRouter
import com.koai.base.main.extension.navigatorViewModel
import com.koai.base.main.screens.BaseScreen
import com.koai.base.utils.LogUtils
import com.twt.launcheros.MainNavigator
import com.twt.launcheros.R

abstract class IScreen<T : ViewBinding, Router : BaseRouter>(layoutId: Int) :
    BaseScreen<T, Router, MainNavigator>(layoutId) {
    override val navigator: MainNavigator by navigatorViewModel()

    override fun initView(savedInstanceState: Bundle?, binding: T) {
        navigator.tag = this::class.java.simpleName
    }

    fun calculateResizeScreen(
        pointTop: View = binding.root.findViewById(R.id.point_top),
        pointBot: View = binding.root.findViewById(R.id.point_bot)
    ) {
        val windowInsetsController =
            WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        try {
            val layoutParamPointTop =
                pointTop.layoutParams as ViewGroup.MarginLayoutParams
            layoutParamPointTop.topMargin = activity.statusBarHeight
            pointTop.layoutParams = layoutParamPointTop

            val layoutParamPointBot =
                pointBot.layoutParams as ViewGroup.MarginLayoutParams
            layoutParamPointBot.bottomMargin = activity.bottomNavigationHeight
            pointBot.layoutParams = layoutParamPointBot
        } catch (e: Exception) {
            LogUtils.log("Error in Margin", e.message ?: "Unknown Error!")
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_bottom)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_bottom)
        }
    }

}