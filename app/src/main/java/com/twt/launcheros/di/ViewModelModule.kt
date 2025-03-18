package com.twt.launcheros.di

import com.koai.base.main.extension.journeyViewModel
import com.twt.launcheros.MainViewModel
import com.twt.launcheros.ui.allApps.AllAppsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    fun init() =
        module {
            viewModel { MainViewModel(get()) }
            journeyViewModel { AllAppsViewModel(get()) }
//        screenViewModel { SplashViewModel() }
//        journeyViewModel { OnboardViewModel() }
//        screenViewModel { OnBoardingViewModel() }
        }
}
