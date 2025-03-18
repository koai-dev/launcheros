package com.twt.launcheros.ui.allApps

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.koai.base.main.viewmodel.BaseViewModel
import com.twt.launcheros.model.AppModel
import com.twt.launcheros.repository.home.HomeRepo
import com.twt.launcheros.service.ApplicationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

class AllAppsViewModel(repository: HomeRepo, private val applicationService: ApplicationService) : BaseViewModel() {
    private val searchQuery = MutableStateFlow("")
    val dockApps = MutableStateFlow<List<AppModel>>(emptyList())

    init {
        getApps()
        getDockApps()
    }

    fun searchApps(text: String = "") {
        searchQuery.value = text
    }

    var launcherApps =
        repository.execute().cachedIn(viewModelScope).combine(searchQuery) { pagingData, searchQuery ->
            pagingData to searchQuery
        }

    private fun getApps() {
        launchCoroutine {
            launcherApps.collect()
        }
    }

    private fun getDockApps() {
        launchCoroutine {
            dockApps.value = applicationService.fetchApplications().filter { it.isPinned == true }
        }
    }
}
