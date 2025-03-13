package com.twt.launcheros.service

import android.content.pm.ApplicationInfo

interface ApplicationService {
    suspend fun fetchApplications(): List<ApplicationInfo>
}