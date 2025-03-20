package com.twt.launcheros.di

import com.twt.launcheros.service.ApplicationService
import com.twt.launcheros.service.ApplicationServiceImpl
import com.twt.launcheros.service.WidgetService
import com.twt.launcheros.service.WidgetServiceImpl
import com.twt.launcheros.utils.ScreenUtilsWrapper
import com.twt.launcheros.worker.WallpaperWorkerWrapper
import com.twt.launcheros.worker.WallpaperWorkerWrapperImpl
import org.koin.dsl.module

object ServiceModule {
    fun init() =
        module {
            single { ScreenUtilsWrapper(get()) }
            factory<ApplicationService> { ApplicationServiceImpl(get()) }
            factory<WidgetService> { WidgetServiceImpl(get()) }
            factory<WallpaperWorkerWrapper> { WallpaperWorkerWrapperImpl(get()) }
        }
}
