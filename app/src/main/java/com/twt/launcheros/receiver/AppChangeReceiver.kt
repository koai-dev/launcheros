package com.twt.launcheros.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.koai.base.utils.LogUtils

class AppChangeReceiver private constructor(
    private var onAppChange: (() -> Unit)? = null
) : BroadcastReceiver() {
    companion object {
        private var instance: AppChangeReceiver? = null

        fun getInstance(onAppChange: (() -> Unit)? = null) =
            if (instance == null) {
                instance =
                    AppChangeReceiver().apply {
                        onAppChange?.let {
                            this.onAppChange = it
                        }
                    }
                instance!!
            } else {
                instance!!
            }
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val action = intent.action
        val data = intent.data
        if (data != null) {
            if (Intent.ACTION_PACKAGE_ADDED == action) {
                LogUtils.log(this::class.java.simpleName, "ACTION_PACKAGE_ADDED")
                onAppChange?.invoke()
            } else if (Intent.ACTION_PACKAGE_REMOVED == action) {
                LogUtils.log(this::class.java.simpleName, "ACTION_PACKAGE_REMOVED")
                onAppChange?.invoke()
            } else if (Intent.ACTION_PACKAGE_REPLACED == action) {
                LogUtils.log(this::class.java.simpleName, "ACTION_PACKAGE_REPLACED")
                onAppChange?.invoke()
            }
        }
    }
}
