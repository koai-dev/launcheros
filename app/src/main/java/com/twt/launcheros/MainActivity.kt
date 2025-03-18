package com.twt.launcheros

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.koai.base.main.BaseActivity
import com.koai.base.main.action.router.BaseRouter
import com.koai.base.widgets.BaseLoadingView
import com.twt.launcheros.databinding.ActivityMainBinding
import com.twt.launcheros.utils.ScreenUtilsWrapper
import com.twt.launcheros.utils.isCurrentLauncher
import com.twt.launcheros.utils.widgets.SwipeGestureListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity :
    BaseActivity<ActivityMainBinding, BaseRouter, MainNavigator>(R.layout.activity_main) {
    private val screenUtilsWrapper: ScreenUtilsWrapper by inject()
    override val navigator: MainNavigator by viewModel()
    private val viewModel by viewModel<MainViewModel>()
    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(
            this@MainActivity,
            SwipeGestureListener(this@MainActivity, screenUtilsWrapper) {
                navigator.onSwipeUp()
            },
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.setWallpaperWorker()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(
        savedInstanceState: Bundle?,
        binding: ActivityMainBinding,
    ) {
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top.takeIf { statusBarHeight -> statusBarHeight > 0 }
                ?.let { height ->
                    statusBarHeight = height
                }
            bottomNavigationHeight =
                windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
            if (windowInsets.isVisible(WindowInsetsCompat.Type.ime())) {
                binding.root.setPadding(
                    0,
                    0,
                    0,
                    windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom,
                )
            } else {
                binding.root.setPadding(0, 0, 0, 0)
            }
            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }
        if (!isCurrentLauncher(this)) {
            // todo show ui popup before navigate to home
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        }
        viewModel.setWallpaperWorker()
    }

    override fun getLoadingView(): View {
        return super.getLoadingView().apply {
            (this is BaseLoadingView)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            gestureDetector.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }
}
