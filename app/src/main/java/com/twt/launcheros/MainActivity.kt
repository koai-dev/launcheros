package com.twt.launcheros

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.koai.base.main.BaseActivity
import com.koai.base.main.action.router.BaseRouter
import com.koai.base.widgets.BaseLoadingView
import com.twt.launcheros.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, BaseRouter, MainNavigator>(R.layout.activity_main) {

    override val navigator: MainNavigator by viewModel()
    var windowInsets: WindowInsetsCompat? = null

    override fun initView(savedInstanceState: Bundle?, binding: ActivityMainBinding) {
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
            this.windowInsets = windowInsets
            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }
    }

    override fun getLoadingView(): View {
        return super.getLoadingView().apply {
            (this is BaseLoadingView)
        }
    }
    /**
     * A native method that is implemented by the 'launcheros' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'launcheros' library on application startup.
        init {
            System.loadLibrary("launcheros")
        }
    }
}