package com.twt.launcheros.di

import com.twt.launcheros.repository.home.HomeRepo
import com.twt.launcheros.repository.home.HomeRepoImpl
import org.koin.dsl.module

object RepositoryModule {
    fun init() = module {
        factory<HomeRepo> { HomeRepoImpl(get()) }
    }
}