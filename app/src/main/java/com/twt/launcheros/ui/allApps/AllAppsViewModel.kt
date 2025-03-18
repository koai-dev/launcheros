package com.twt.launcheros.ui.allApps

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.koai.base.main.viewmodel.BaseViewModel
import com.twt.launcheros.repository.home.HomeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class AllAppsViewModel(repository: HomeRepo) : BaseViewModel() {
    private val searchQuery = MutableStateFlow("")

    fun searchApps(text: String = "") {
        searchQuery.value = text
    }

    var launcherApps =
        repository.execute().cachedIn(viewModelScope).combine(searchQuery) { pagingData, searchQuery ->
            pagingData to searchQuery
        }
}
