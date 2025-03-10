package com.twt.launcheros.ui.home

import android.content.pm.ApplicationInfo
import androidx.lifecycle.MutableLiveData
import com.koai.base.main.viewmodel.BaseViewModel
import com.koai.base.network.ResponseStatus
import com.twt.launcheros.repository.home.HomeRepo

class HomeViewModel(private val repository: HomeRepo): BaseViewModel() {
    private val _launcherApps = MutableLiveData<ResponseStatus<List<ApplicationInfo>>>()
    val launcherApps: MutableLiveData<ResponseStatus<List<ApplicationInfo>>> = _launcherApps

    fun fetchLauncherApps() {
        launchCoroutine {
            val apps = repository.fetchApplications()
            _launcherApps.postValue(ResponseStatus.Success(data = apps))
        }
    }
}