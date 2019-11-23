package com.test.tabscontentloadingtest.di

import com.test.tabscontentloadingtest.data.repository.TextRepository
import com.test.tabscontentloadingtest.data.source.MemoryCacheDataSource
import com.test.tabscontentloadingtest.data.source.NetworkDataSource
import com.test.tabscontentloadingtest.domain.PageInteractor

object InstanceProvider {
    private val memoryCacheDataSource = MemoryCacheDataSource()
    private val networkDataSource = NetworkDataSource()
    private val textRepository = TextRepository(memoryCacheDataSource, networkDataSource)
    val pageInteractor = PageInteractor(textRepository)
}
