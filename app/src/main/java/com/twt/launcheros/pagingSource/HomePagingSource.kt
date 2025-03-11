package com.twt.launcheros.pagingSource

import android.content.pm.ApplicationInfo
import com.koai.base.main.paging.IPagingSource
import com.twt.launcheros.service.HomeService
import kotlin.math.min

class HomePagingSource(private val homeService: HomeService) : IPagingSource<ApplicationInfo>() {
    override suspend fun loadData(
        startKey: Int,
        rangeKey: IntRange,
        prevKey: Int?,
        nextKey: Int?
    ): LoadResult<Int, ApplicationInfo> {
        try {
            val allApps = homeService.fetchApplications()
            val apps = allApps.subList(startKey, min(rangeKey.last, allApps.size))
            return LoadResult.Page(data = apps, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception){
            return LoadResult.Error(e)
        }
    }
}