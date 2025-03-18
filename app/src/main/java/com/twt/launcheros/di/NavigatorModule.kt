package com.twt.launcheros.di

import com.koai.base.main.extension.navigatorViewModel
import com.twt.launcheros.MainNavigator
import org.koin.dsl.module

object NavigatorModule {
    fun init() =
        module {
            navigatorViewModel { MainNavigator() }
        }
}
