package com.twt.launcheros.repository.home

import com.koai.base.main.paging.PagingRepository
import com.twt.launcheros.model.AppModel
import com.twt.launcheros.pagingSource.HomePagingSource

class HomeRepo(private val homePagingSource: HomePagingSource) : PagingRepository<AppModel>() {
    override fun pagingSource() = homePagingSource

    override fun pageSize() = 100
}
