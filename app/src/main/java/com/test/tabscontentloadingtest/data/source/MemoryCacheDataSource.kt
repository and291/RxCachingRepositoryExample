package com.test.tabscontentloadingtest.data.source

import android.util.Log
import android.util.SparseArray
import com.test.tabscontentloadingtest.domain.Constants
import com.test.tabscontentloadingtest.domain.entity.GetTextResult
import io.reactivex.Completable
import io.reactivex.Maybe

class MemoryCacheDataSource {

    private val cache = SparseArray<GetTextResult>()

    fun getText(pageNumber: Int): Maybe<GetTextResult> = Maybe.fromCallable {
        synchronized(this@MemoryCacheDataSource) {
            cache.get(pageNumber)
                .also { Log.d(LOG_TAG, "Requested by #$pageNumber, result=$it") }
        }
    }

    fun putText(pageNumber: Int, result: GetTextResult): Completable = Completable.fromCallable {
        synchronized(this@MemoryCacheDataSource) {
            cache.put(pageNumber, result)
            Log.d(LOG_TAG, "Put into #$pageNumber, result=$result")
        }
    }

    companion object {
        private const val LOG_TAG = "${Constants.LOG_PREFIX} MemoryCacheDataSource"
    }
}