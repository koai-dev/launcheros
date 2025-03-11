package com.twt.launcheros.repository.home

import android.content.pm.ApplicationInfo
import com.koai.base.main.paging.PagingRepository
import com.twt.launcheros.pagingSource.HomePagingSource

class HomeRepo(private val homePagingSource: HomePagingSource) : PagingRepository<ApplicationInfo>(){
    override fun pagingSource() = homePagingSource
    override fun pageSize() = 100
}