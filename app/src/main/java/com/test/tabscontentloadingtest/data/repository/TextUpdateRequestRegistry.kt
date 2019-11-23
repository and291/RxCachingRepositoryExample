package com.test.tabscontentloadingtest.data.repository

import android.util.Log
import com.test.tabscontentloadingtest.data.repository.TextUpdateRequestRegistry.PutResult.ALREADY_IN_QUEUE
import com.test.tabscontentloadingtest.data.repository.TextUpdateRequestRegistry.PutResult.DOWNLOAD_NEEDED
import com.test.tabscontentloadingtest.domain.Constants.LOG_PREFIX
import com.test.tabscontentloadingtest.domain.entity.GetTextResult
import io.reactivex.SingleEmitter

class TextUpdateRequestRegistry {

    private val requestsInProgress = mutableListOf<RegistryItem>()

    fun put(pageNumber: Int, singleEmitter: SingleEmitter<GetTextResult>): PutResult = synchronized(this) {
        val isOtherRequestInProgress = requestsInProgress.any { it.pageNumber == pageNumber }

        requestsInProgress.add(RegistryItem(pageNumber, singleEmitter))
        Log.d(
            LOG_TAG,
            "Added to queue: isOtherRequestInProgress=$isOtherRequestInProgress for #$pageNumber. Registry size=${requestsInProgress.size}"
        )

        when (isOtherRequestInProgress) {
            true -> ALREADY_IN_QUEUE
            false -> DOWNLOAD_NEEDED
        }
    }

    fun pollRelatedEmitters(pageNumber: Int): List<SingleEmitter<GetTextResult>> = synchronized(this) {
        val emitters = mutableListOf<SingleEmitter<GetTextResult>>()
        val mutableIterator = requestsInProgress.iterator()
        for (registryItem in mutableIterator) {
            if (registryItem.pageNumber == pageNumber) {
                emitters.add(registryItem.singleEmitter)
                mutableIterator.remove()
            }
        }

        Log.d(
            LOG_TAG,
            "List of emitters to notify formed: size=${emitters.size}. Registry size=${requestsInProgress.size}"
        )
        emitters.toList()
    }

    enum class PutResult {
        ALREADY_IN_QUEUE,
        DOWNLOAD_NEEDED
    }

    private data class RegistryItem(
        val pageNumber: Int,
        val singleEmitter: SingleEmitter<GetTextResult>
    )

    companion object {
        private const val LOG_TAG = "$LOG_PREFIX TextUpdateRequestRegistry"
    }
}
