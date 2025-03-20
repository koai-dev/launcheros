package com.twt.launcheros

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.koai.base.main.viewmodel.BaseViewModel
import com.twt.launcheros.model.AppModel
import com.twt.launcheros.pagingSource.HomePagingSource
import com.twt.launcheros.repository.home.HomeRepo
import com.twt.launcheros.service.ApplicationService
import com.twt.launcheros.worker.WallpaperWorkerWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

class MainViewModel(private val wallpaperWorkerWrapper: WallpaperWorkerWrapper, private var repository: HomeRepo, private val applicationService: ApplicationService) : BaseViewModel() {
    private val searchQuery = MutableStateFlow("")
    val dockApps = MutableStateFlow<List<AppModel>>(emptyList())

    init {
        getApps()
    }

    fun searchApps(text: String? = null) {
        searchQuery.value = text ?: searchQuery.value
    }

    var launcherApps =
        HomeRepo(HomePagingSource(applicationService)).execute().cachedIn(viewModelScope).combine(searchQuery) { pagingData, searchQuery ->
            pagingData to searchQuery
        }

    fun getApps() {
        launchCoroutine {
            dockApps.value = applicationService.fetchApplications().filter { it.isPinned == true }.sortedByDescending { it.order }.take(5)
            launcherApps =
                HomeRepo(HomePagingSource(applicationService)).execute().cachedIn(viewModelScope).combine(searchQuery) { pagingData, searchQuery ->
                    pagingData to searchQuery
                }
            launcherApps.collect()
        }
    }

    fun setWallpaperWorker() {
        wallpaperWorkerWrapper.execute()
    }
}
