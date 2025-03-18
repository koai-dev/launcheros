package com.twt.launcheros.model

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.Keep
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

fun ResolveInfo.toAppModel(packageManager: PackageManager) =
    AppModel(
        label = this.loadLabel(packageManager).toString(),
        packageName = this.activityInfo.packageName,
        icon = this.loadIcon(packageManager),
    )
