package com.twt.launcheros.service

import com.twt.launcheros.model.AppModel

interface ApplicationService {
    suspend fun fetchApplications(): List<AppModel>
}