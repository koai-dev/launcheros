package com.twt.launcheros.model

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.Keep
import com.twt.launcheros.utils.isDefaultBrowser
import com.twt.launcheros.utils.isDefaultCamera
import com.twt.launcheros.utils.isDefaultDialer
import com.twt.launcheros.utils.isDefaultSmsPackage
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
data class AppModel(
    val label: String,
    val packageName: String,
    val isNew: Boolean? = false,
    val icon: @RawValue Drawable? = null,
    val isLimited: Boolean? = false,
    val isPinned: Boolean? = false,
    val timeLimit: Long? = 0,
    val lastTimeUsed: String? = null,
    val timeSpent: String? = null,
) : Parcelable

fun ResolveInfo.toAppModel(context: Context, packageManager: PackageManager) =
    AppModel(
        label = this.loadLabel(packageManager).toString(),
        packageName = this.activityInfo.packageName,
        icon = this.loadIcon(packageManager),
        isPinned = defaultPinnedApp(context, this.activityInfo.packageName)
    )

fun defaultPinnedApp(context: Context, packageName: String): Boolean{
    return isDefaultDialer(context, packageName) || isDefaultSmsPackage(context, packageName) || isDefaultBrowser(context, packageName) || isDefaultCamera(context, packageName)
}