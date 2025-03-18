package com.twt.launcheros

import com.koai.base.main.action.navigator.BaseNavigator
import com.twt.launcheros.ui.allApps.AllAppsRouter
import com.twt.launcheros.ui.home.HomeRouter
import com.twt.launcheros.ui.home.HomeScreen

class MainNavigator : BaseNavigator(), HomeRouter, AllAppsRouter {
    var tag: String = this::class.java.simpleName

    override fun gotoAllApps() {
        offNavScreen(R.id.action_global_allAppsScreen)
    }

    fun onSwipeUp() {
        if (tag == HomeScreen::class.java.simpleName) {
            gotoAllApps()
        }
    }

    override fun gotoSetting() {
        offNavScreen(R.id.action_global_settingsScreen)
    }
}
