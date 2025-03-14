package com.twt.launcheros.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

fun isCurrentLauncher(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }
    val packageManager = context.packageManager
    val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfo?.activityInfo?.packageName  == context.packageName
}