package com.twt.launcheros.ui.allApps

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.filter
import com.koai.base.main.action.router.BaseRouter
import com.koai.base.main.extension.safeClick
import com.koai.base.main.extension.screenViewModel
import com.twt.launcheros.R
import com.twt.launcheros.databinding.ScreenAllAppsBinding
import com.twt.launcheros.ui.IScreen
import com.twt.launcheros.utils.widgets.PreCachingLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AllAppsScreen : IScreen<ScreenAllAppsBinding, BaseRouter>(R.layout.screen_all_apps) {
    override val viewModel: AllAppsViewModel by screenViewModel()
    private val adapter = AllAppsAdapter()
    override fun initView(savedInstanceState: Bundle?, binding: ScreenAllAppsBinding) {
        calculateResizeScreen()
        setupGridView()
        searchView()
        observer()
    }

    private fun observer() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.launcherApps.collectLatest { data ->
                    adapter.submitData(data.first.filter { item ->
                        item.loadLabel(activity.packageManager).contains(
                            data.second, ignoreCase = true
                        )
                    })
                }
            }
        }
    }

    private fun setupGridView() {
        binding.grid.apply {
            val layoutMg = PreCachingLayoutManager(binding.grid.context, 5)
            layoutManager = layoutMg
            itemAnimator = null
            adapter = this@AllAppsScreen.adapter
        }
    }

    private fun searchView() {
        binding.searchView.doOnTextChanged { text, _, _, _ ->
            binding.clearButton.isVisible = !text.isNullOrEmpty()
            text?.let {
                viewModel.searchApps(text.toString())
            }
        }
        binding.clearButton.safeClick {
            binding.searchView.text.clear()
        }
    }
}