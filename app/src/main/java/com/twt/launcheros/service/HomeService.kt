package com.twt.launcheros.service

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

interface HomeService {
    suspend fun fetchApplications(): List<ApplicationInfo>
}