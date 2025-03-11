package com.twt.launcheros.di

import com.twt.launcheros.service.HomeService
import com.twt.launcheros.service.HomeServiceImpl
import org.koin.dsl.module

object ServiceModule {
    fun init() = module {
        factory<HomeService> { HomeServiceImpl(get()) }
    }
}