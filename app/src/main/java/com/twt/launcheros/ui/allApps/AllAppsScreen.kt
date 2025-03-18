package com.twt.launcheros.ui.allApps

import android.os.Build
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.filter
import com.koai.base.main.extension.journeyViewModel
import com.koai.base.main.extension.safeClick
import com.koai.base.utils.LogUtils
import com.twt.launcheros.R
import com.twt.launcheros.databinding.ScreenAllAppsBinding
import com.twt.launcheros.ui.IScreen
import com.twt.launcheros.utils.widgets.PreCachingLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AllAppsScreen : IScreen<ScreenAllAppsBinding, AllAppsRouter>(R.layout.screen_all_apps) {
    override val viewModel: AllAppsViewModel by journeyViewModel()
    private val adapter =
        AllAppsAdapter { item ->
            LogUtils.log("CLICK ITEM", item.packageName)
            try {
                if (item.packageName != "com.android.settings") {
                    val intent =
                        binding.root.context.packageManager.getLaunchIntentForPackage(item.packageName)
                    binding.root.context.startActivity(intent)
                } else {
                    router?.gotoSetting()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override fun initView(
        savedInstanceState: Bundle?,
        binding: ScreenAllAppsBinding,
    ) {
        super.initView(savedInstanceState, binding)
        calculateResizeScreen()
        setupGridView()
        searchView()
        observer()
    }

    private fun observer() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.launcherApps.collectLatest { data ->
                    binding.grid.layoutManager = PreCachingLayoutManager(binding.grid.context, 5, reverseLayout = data.second.isNotEmpty())
                    adapter.submitData(
                        data.first.filter { item ->
                            item.label.contains(
                                data.second,
                                ignoreCase = true,
                            )
                        },
                    )
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                frameContentVelocity = 1000f
            }
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
