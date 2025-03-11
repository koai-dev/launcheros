package com.twt.launcheros.ui.home

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.koai.base.main.action.router.BaseRouter
import com.koai.base.main.extension.screenViewModel
import com.twt.launcheros.R
import com.twt.launcheros.databinding.ScreenHomeBinding
import com.twt.launcheros.ui.IScreen
import com.twt.launcheros.utils.widgets.PreCachingLayoutManager
import kotlinx.coroutines.launch

class HomeScreen: IScreen<ScreenHomeBinding, BaseRouter>(R.layout.screen_home) {
    override val viewModel: HomeViewModel by screenViewModel()
    private val adapter = HomeAdapter()
    override fun initView(savedInstanceState: Bundle?, binding: ScreenHomeBinding) {
        calculateResizeScreen()
        setupGridView()
        observer()
    }

    private fun observer(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.launcherApps.collect{
                    adapter.submitData(it)
                }
            }
        }
    }

    override fun setupOnBackPressEvent() {
        super.setupOnBackPressEvent()
    }

    private fun setupGridView(){
        binding.grid.apply {
            val layoutMg = PreCachingLayoutManager(binding.grid.context, 5)
            layoutManager = layoutMg
            itemAnimator = null
            adapter = this@HomeScreen.adapter
        }
    }
}