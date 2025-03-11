package com.twt.launcheros.di

import com.twt.launcheros.repository.home.HomeRepo
import org.koin.dsl.module

object RepositoryModule {
    fun init() = module {
        factory { HomeRepo(get()) }
    }
}