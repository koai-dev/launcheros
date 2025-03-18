package com.twt.launcheros.ui.home

import android.os.Bundle
import com.twt.launcheros.R
import com.twt.launcheros.databinding.ScreenHomeBinding
import com.twt.launcheros.ui.IScreen
import com.twt.launcheros.ui.home.homeScreenFragment.HomeScreenFragment

class HomeScreen : IScreen<ScreenHomeBinding, HomeRouter>(R.layout.screen_home) {
    override fun initView(
        savedInstanceState: Bundle?,
        binding: ScreenHomeBinding,
    ) {
        super.initView(savedInstanceState, binding)
        calculateResizeScreen()
        setupPager()
    }

    private fun setupPager() {
        binding.pager.apply {
            adapter = HomePagerAdapter(this@HomeScreen, arrayListOf(HomeScreenFragment.newInstance(emptyList())))
            offscreenPageLimit = 1
            isUserInputEnabled = true
        }
    }
}
