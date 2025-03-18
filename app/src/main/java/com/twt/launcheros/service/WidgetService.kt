package com.twt.launcheros.service

import android.appwidget.AppWidgetProviderInfo

interface WidgetService {
    suspend fun execute(): List<AppWidgetProviderInfo>
}
