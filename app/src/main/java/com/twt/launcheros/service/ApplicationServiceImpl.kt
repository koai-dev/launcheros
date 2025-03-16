package com.twt.launcheros.service

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class ApplicationServiceImpl(private val context: Context) : ApplicationService {
    override suspend fun fetchApplications(): List<ApplicationInfo> {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.packageName != "com.android.settings" }
            .sortedBy {item-> item.loadLabel(context.packageManager).toString() }
        val launcherApps = mutableListOf<ApplicationInfo>()

        for (app in apps) {
            val intent = pm.getLaunchIntentForPackage(app.packageName)
            if (intent != null && app.packageName != context.packageName) {
                launcherApps.add(app)
            }
        }
        return launcherApps
    }
}