package com.twt.launcheros.ui.home

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.koai.base.main.extension.journeyViewModel
import com.koai.base.main.extension.safeClick
import com.twt.launcheros.MainViewModel
import com.twt.launcheros.R
import com.twt.launcheros.databinding.ItemDockAppBinding
import com.twt.launcheros.databinding.ScreenHomeBinding
import com.twt.launcheros.ui.IScreen
import com.twt.launcheros.ui.home.homeScreenFragment.HomeScreenFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeScreen : IScreen<ScreenHomeBinding, HomeRouter>(R.layout.screen_home) {
    override val viewModel by journeyViewModel<MainViewModel>()

    override fun initView(
        savedInstanceState: Bundle?,
        binding: ScreenHomeBinding,
    ) {
        super.initView(savedInstanceState, binding)
        calculateResizeScreen()
        setupPager()
        observer()
    }

    private fun setupPager() {
        binding.pager.apply {
            adapter =
                HomePagerAdapter(
                    this@HomeScreen,
                    arrayListOf(HomeScreenFragment.newInstance(emptyList())),
                )
            offscreenPageLimit = 1
            isUserInputEnabled = true
        }
    }

    private fun observer() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dockApps.collect { data ->
                    binding.ctnFooter.removeAllViews()
                    data.forEach { item ->
                        val dockItemBinding = ItemDockAppBinding.inflate(layoutInflater)
                        dockItemBinding.icon.load(item.icon)
                        dockItemBinding.root.safeClick {
                            try {
                                val intent =
                                    binding.root.context.packageManager.getLaunchIntentForPackage(
                                        item.packageName,
                                    )
                                binding.root.context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        binding.ctnFooter.addView(dockItemBinding.root)
                    }
                }
            }
        }
    }
}
