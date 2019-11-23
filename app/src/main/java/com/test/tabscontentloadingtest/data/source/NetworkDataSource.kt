package com.test.tabscontentloadingtest.data.source

import android.util.Log
import com.test.tabscontentloadingtest.domain.Constants.LOG_PREFIX
import com.test.tabscontentloadingtest.domain.entity.GetTextResult
import io.reactivex.Single
import java.util.*

class NetworkDataSource {

    fun getText(pageNumber: Int): Single<GetTextResult> = Single.fromCallable {
        Log.d(LOG_TAG, "Start loading for #$pageNumber")
        try {
            simulateLongRunningTask()
            GetTextResult.Success(System.currentTimeMillis(), UUID.randomUUID().toString())
        } catch (e: Exception) {
            GetTextResult.Error(System.currentTimeMillis(), e)
        }.also { Log.d(LOG_TAG, "Finish loading for #$pageNumber with result=$it") }
    }

    private fun simulateLongRunningTask() {
        val endTime = System.currentTimeMillis() + 25_000L

        while (true) {
            if (System.currentTimeMillis() > endTime) {
                break
            }

            try {
                Thread.sleep(100)
            } catch (e: Exception) {
                //do nothing
            }
        }

        if (Random().nextBoolean()) {
            throw Exception("Synthetic exception")
        }
    }

    companion object {
        private const val LOG_TAG = "$LOG_PREFIX NetworkDataSource"
    }
}