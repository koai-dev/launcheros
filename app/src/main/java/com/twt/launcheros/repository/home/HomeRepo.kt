package com.twt.launcheros.repository.home

import android.content.pm.ApplicationInfo

interface HomeRepo {
    suspend fun fetchApplications(): List<ApplicationInfo>
}