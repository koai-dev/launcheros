package com.twt.launcheros.ui.home

import android.os.Bundle
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.koai.base.main.action.router.BaseRouter
import com.koai.base.main.extension.screenViewModel
import com.koai.base.network.ResponseStatus
import com.twt.launcheros.R
import com.twt.launcheros.databinding.ScreenHomeBinding
import com.twt.launcheros.ui.IScreen
import com.twt.launcheros.utils.widgets.PreCachingLayoutManager

class HomeScreen: IScreen<ScreenHomeBinding, BaseRouter>(R.layout.screen_home) {
    override val viewModel: HomeViewModel by screenViewModel()
    private val adapter = HomeAdapter()
    override fun initView(savedInstanceState: Bundle?, binding: ScreenHomeBinding) {
        setupGridView()
        observer()
        getApps()
    }

    private fun getApps(){
        viewModel.fetchLauncherApps()
    }

    private fun observer(){
        viewModel.launcherApps.observe(viewLifecycleOwner){status->
            when(status){
                is ResponseStatus.Success -> {
                    hideLoading()
                    adapter.submitList(status.data)
                }
                else -> showLoading()
            }
        }
    }

    override fun setupOnBackPressEvent() {
        super.setupOnBackPressEvent()
    }

    private fun setupGridView(){
        binding.grid.apply {

            val layoutMg = PreCachingLayoutManager(activity, 4)
            layoutMg.isSmoothScrollbarEnabled = true
            setItemViewCacheSize(200)
            setRecycledViewPool(RecyclerView.RecycledViewPool())
            setHasFixedSize(true)
            layoutManager = layoutMg
            itemAnimator = null
            adapter = this@HomeScreen.adapter
        }
    }
}