package com.twt.launcheros.di

import com.twt.launcheros.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    fun init() =
        module {
            viewModel { MainViewModel(get(), get(), get()) }
        }
}
