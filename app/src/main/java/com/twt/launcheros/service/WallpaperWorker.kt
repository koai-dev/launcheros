package com.twt.launcheros.service

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.koai.base.utils.EncryptPreference
import com.twt.launcheros.utils.Constants
import com.twt.launcheros.utils.getTodaysWallpaper
import com.twt.launcheros.utils.isDarkThemeOn
import com.twt.launcheros.utils.isOlauncherDefault
import com.twt.launcheros.utils.setWallpaper
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit

class WallpaperWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    private val prefs = EncryptPreference(applicationContext)

    override suspend fun doWork(): Result =
        coroutineScope {
            val success =
                if (isOlauncherDefault(applicationContext).not()) {
                    true
                } else if (!prefs.getBooleanPref(Constants.Prefs.DAILY_WALLPAPER)) { // todo !
                    val wallType = checkWallpaperType()
                    val wallpaperUrl = getTodaysWallpaper(wallType, prefs.getLongPref(Constants.Prefs.FIRST_OPEN_TIME))
                    if (prefs.getStringPref(Constants.Prefs.DAILY_WALLPAPER_URL) == wallpaperUrl) {
                        true
                    } else {
                        prefs.setStringPref(Constants.Prefs.DAILY_WALLPAPER_URL, wallpaperUrl)
                        setWallpaper(applicationContext, wallpaperUrl)
                    }
                } else {
                    true
                }

            if (success) {
                Result.success()
            } else {
                Result.retry()
            }
        }

    private fun checkWallpaperType(): String {
        return when (prefs.getIntPref(Constants.Prefs.APP_THEME)) {
            AppCompatDelegate.MODE_NIGHT_YES -> Constants.WALL_TYPE_DARK
            AppCompatDelegate.MODE_NIGHT_NO -> Constants.WALL_TYPE_LIGHT
            else ->
                if (applicationContext.isDarkThemeOn()) {
                    Constants.WALL_TYPE_DARK
                } else {
                    Constants.WALL_TYPE_LIGHT
                }
        }
    }
}

interface WallpaperWorkerWrapper {
    fun execute()
}

class WallpaperWorkerWrapperImpl(private val context: Context) : WallpaperWorkerWrapper {
    override fun execute() {
        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val uploadWorkRequest =
            PeriodicWorkRequestBuilder<WallpaperWorker>(8, TimeUnit.HOURS)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                Constants.WALLPAPER_WORKER_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                uploadWorkRequest,
            )
    }
}
