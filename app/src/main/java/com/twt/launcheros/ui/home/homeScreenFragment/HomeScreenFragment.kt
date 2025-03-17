package com.twt.launcheros.ui.home.homeScreenFragment

import android.content.pm.ApplicationInfo
import android.os.Bundle
import com.koai.base.main.action.router.BaseRouter
import com.twt.launcheros.R
import com.twt.launcheros.databinding.FragmentHomeScreenBinding
import com.twt.launcheros.ui.IScreen

class HomeScreenFragment: IScreen<FragmentHomeScreenBinding, BaseRouter>(R.layout.fragment_home_screen) {
    override fun initView(savedInstanceState: Bundle?, binding: FragmentHomeScreenBinding) {

    }

    companion object {
        fun newInstance(items: List<ApplicationInfo>): HomeScreenFragment {
            val args = Bundle()
            val fragment = HomeScreenFragment()
            fragment.arguments = args
            return fragment
        }
    }
}