package com.twt.launcheros.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.koai.base.utils.LogUtils
import com.twt.launcheros.MainNavigator
import com.twt.launcheros.di.event.AppChangeEvent

class AppChangeReceiver private constructor() : BroadcastReceiver() {
    companion object {
        private var instance: AppChangeReceiver? = null

        fun getInstance(navigator: MainNavigator? = null) =
            if (instance == null)
                {
                    instance =
                        AppChangeReceiver().apply {
                            this.navigator = navigator
                        }
                    instance!!
                } else {
                instance!!
            }
    }

    private var navigator: MainNavigator? = null

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val action = intent.action
        val data = intent.data
        if (data != null) {
            if (Intent.ACTION_PACKAGE_ADDED == action) {
                LogUtils.log(this::class.java.simpleName, "ACTION_PACKAGE_ADDED $navigator")
                navigator?.sendEvent(AppChangeEvent())
            } else if (Intent.ACTION_PACKAGE_REMOVED == action) {
                LogUtils.log(this::class.java.simpleName, "ACTION_PACKAGE_REMOVED $navigator")
                navigator?.sendEvent(AppChangeEvent())
            } else if (Intent.ACTION_PACKAGE_REPLACED == action) {
                LogUtils.log(this::class.java.simpleName, "ACTION_PACKAGE_REPLACED")
                navigator?.sendEvent(AppChangeEvent())
            }
        }
    }
}
