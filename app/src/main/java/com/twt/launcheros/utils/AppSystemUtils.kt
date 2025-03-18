package com.twt.launcheros.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Telephony
import android.telecom.TelecomManager

fun isDefaultDialer(
    context: Context,
    packageName: String,
): Boolean {
    val defaultDialer =
        (context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager)?.defaultDialerPackage
            ?: "com.google.android.contacts"
    return defaultDialer == packageName
}

fun isDefaultCamera(
    context: Context,
    packageName: String,
): Boolean {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val pm = context.packageManager
    val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

    if (resolveInfo?.activityInfo != null) {
        return resolveInfo.activityInfo.packageName == packageName
    }
    return false
}

fun isDefaultBrowser(
    context: Context,
    packageName: String,
): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
    val pm = context.packageManager
    val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

    if (resolveInfo?.activityInfo != null) {
        return resolveInfo.activityInfo.packageName == packageName
    }
    return false
}

fun isDefaultSmsPackage(
    context: Context,
    packageName: String,
): Boolean {
    return Telephony.Sms.getDefaultSmsPackage(context) == packageName
}
