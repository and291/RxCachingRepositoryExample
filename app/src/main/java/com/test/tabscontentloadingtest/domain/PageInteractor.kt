package com.test.tabscontentloadingtest.domain

import com.test.tabscontentloadingtest.data.repository.TextRepository

class PageInteractor(
    private val textRepository: TextRepository
) {

    fun getText(pageNumber: Int) = textRepository.getText(pageNumber)
}
