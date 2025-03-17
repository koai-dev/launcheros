package com.twt.launcheros

import com.koai.base.main.viewmodel.BaseViewModel
import com.twt.launcheros.service.WallpaperWorkerWrapper

class MainViewModel(private val wallpaperWorkerWrapper: WallpaperWorkerWrapper): BaseViewModel() {
    fun setWallpaperWorker() {
        wallpaperWorkerWrapper.execute()
    }
}