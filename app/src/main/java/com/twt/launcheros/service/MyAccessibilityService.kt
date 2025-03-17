package com.twt.launcheros.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.koai.base.utils.EncryptPreference
import com.twt.launcheros.R
import com.twt.launcheros.utils.Constants
import org.koin.android.ext.android.inject

class MyAccessibilityService : AccessibilityService() {
    private val pref by inject<EncryptPreference>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onServiceConnected() {
        pref.setBooleanPref(Constants.Prefs.LOCK_MODE, true)
        super.onServiceConnected()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        try {
            val source: AccessibilityNodeInfo = event.source ?: return
            if ((source.className == "android.widget.FrameLayout") and
                (source.contentDescription == getString(R.string.lock_layout_description))
            )
                performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        } catch (e: Exception) {
            return
        }
    }

    override fun onInterrupt() {

    }
}