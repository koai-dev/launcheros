package com.twt.launcheros.di

import com.twt.launcheros.pagingSource.HomePagingSource
import org.koin.dsl.module

object PagingSourceModule {
    fun init() =
        module {
            factory { HomePagingSource(get(), get()) }
        }
}
