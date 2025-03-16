package com.twt.launcheros

import com.koai.base.main.action.navigator.BaseNavigator
import com.twt.launcheros.ui.home.HomeRouter

class MainNavigator : BaseNavigator(), HomeRouter {

    override fun gotoAllApps() {
        offNavScreen(R.id.action_global_allAppsScreen)
    }
}