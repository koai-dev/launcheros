package com.twt.launcheros.ui.home

import android.os.Bundle
import com.twt.launcheros.R
import com.twt.launcheros.databinding.ScreenHomeBinding
import com.twt.launcheros.ui.IScreen

class HomeScreen : IScreen<ScreenHomeBinding, HomeRouter>(R.layout.screen_home) {
    override fun initView(
        savedInstanceState: Bundle?,
        binding: ScreenHomeBinding,
    ) {
        super.initView(savedInstanceState, binding)
        calculateResizeScreen()
    }
}
