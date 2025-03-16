package com.twt.launcheros.di

import com.koai.base.main.extension.screenViewModel
import com.twt.launcheros.ui.allApps.AllAppsViewModel
import org.koin.dsl.module

object ViewModelModule {
    fun init() = module {
        screenViewModel { AllAppsViewModel(get()) }
//        screenViewModel { SplashViewModel() }
//        journeyViewModel { OnboardViewModel() }
//        screenViewModel { OnBoardingViewModel() }
    }
}