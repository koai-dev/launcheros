package com.twt.launcheros.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.koai.base.main.viewmodel.BaseViewModel
import com.twt.launcheros.repository.home.HomeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class HomeViewModel(repository: HomeRepo) : BaseViewModel() {
    private val searchQuery = MutableStateFlow("")
    fun searchApps(text: String = "") {
        searchQuery.value = text
    }

    var launcherApps = repository.execute().cachedIn(viewModelScope).combine(searchQuery) { pagingData, searchQuery ->
        pagingData to searchQuery
    }

}