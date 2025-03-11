package com.twt.launcheros

import com.koai.base.BaseApplication
import com.koai.base.BuildConfig
import com.koai.base.utils.LogUtils
import com.twt.launcheros.di.DBModule
import com.twt.launcheros.di.NavigatorModule
import com.twt.launcheros.di.PagingSourceModule
import com.twt.launcheros.di.RepositoryModule
import com.twt.launcheros.di.ServiceModule
import com.twt.launcheros.di.ViewModelModule
import org.koin.dsl.module

class MyApplication: BaseApplication() {
    override fun appModule() = module {
        includes(
            super.appModule(),
            DBModule.init(),
            PagingSourceModule.init(),
            ServiceModule.init(),
            RepositoryModule.init(),
            NavigatorModule.init(),
            ViewModelModule.init(),
        )
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.setDebugMode(BuildConfig.DEBUG)
    }
}