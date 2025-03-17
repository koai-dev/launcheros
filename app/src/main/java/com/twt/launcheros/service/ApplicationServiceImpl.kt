package com.twt.launcheros.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.twt.launcheros.BuildConfig
import com.twt.launcheros.model.AppModel
import com.twt.launcheros.model.toAppModel

class ApplicationServiceImpl(private val context: Context) : ApplicationService {
    override suspend fun fetchApplications(): List<AppModel> {
        val pm = context.packageManager
        val apps = pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }, PackageManager.MATCH_ALL).sortedBy { item -> item.loadLabel(pm).toString() }
            .map { item -> item.toAppModel(pm) }
            .filter { item -> (item.packageName != BuildConfig.APPLICATION_ID) }
            .sortedBy { item -> item.label }

        return apps
    }
}