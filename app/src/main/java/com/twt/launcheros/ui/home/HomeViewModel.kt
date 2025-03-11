package com.twt.launcheros.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.koai.base.main.viewmodel.BaseViewModel
import com.twt.launcheros.repository.home.HomeRepo

class HomeViewModel(repository: HomeRepo) : BaseViewModel() {
    val launcherApps = repository.execute().cachedIn(viewModelScope)

}