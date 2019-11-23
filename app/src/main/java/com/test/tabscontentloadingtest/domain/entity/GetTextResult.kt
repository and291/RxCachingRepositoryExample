package com.test.tabscontentloadingtest.domain.entity

sealed class GetTextResult {

    abstract val timestamp: Long

    data class Success(
        override val timestamp: Long,
        val text: String
    ) : GetTextResult()

    data class Error(
        override val timestamp: Long,
        val throwable: Throwable
    ) : GetTextResult()
}
