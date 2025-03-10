package com.twt.launcheros.repository.home

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager


class HomeRepoImpl(private val context: Context) : HomeRepo {
    override suspend fun fetchApplications(): List<ApplicationInfo> {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val launcherApps = mutableListOf<ApplicationInfo>()

        for (app in apps) {
            val intent = pm.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                launcherApps.add(app)
            }
        }
        return apps
    }

}