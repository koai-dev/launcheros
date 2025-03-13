package com.twt.launcheros.service

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context

class WidgetServiceImpl(private val context: Context): WidgetService {
    override suspend fun execute(): List<AppWidgetProviderInfo> {
        val appWidgetManager = AppWidgetManager.getInstance(context)
         return appWidgetManager.installedProviders
    }
}