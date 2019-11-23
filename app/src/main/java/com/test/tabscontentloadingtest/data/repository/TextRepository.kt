package com.test.tabscontentloadingtest.data.repository

import android.util.Log
import com.test.tabscontentloadingtest.data.source.MemoryCacheDataSource
import com.test.tabscontentloadingtest.data.source.NetworkDataSource
import com.test.tabscontentloadingtest.domain.Constants.LOG_PREFIX
import com.test.tabscontentloadingtest.domain.entity.GetTextResult
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleEmitter

class TextRepository(
    private val memoryCacheDataSource: MemoryCacheDataSource,
    networkDataSource: NetworkDataSource
) : TextNetworkRequestController.Callback {

    private val textUpdateController = TextNetworkRequestController(networkDataSource, this)

    fun getText(pageNumber: Int): Single<GetTextResult> = memoryCacheDataSource
        .getText(pageNumber)
        .filter { it is GetTextResult.Success }
        .switchIfEmpty(textUpdateController.getText(pageNumber))

    //region TextNetworkRequestController.Callback
    override fun onTextLoaded(
        pageNumber: Int,
        getTextResult: GetTextResult,
        emitters: List<SingleEmitter<GetTextResult>>
    ): Completable = memoryCacheDataSource
        .putText(pageNumber, getTextResult)
        .andThen(notifyAllEmitters(pageNumber, getTextResult, emitters))
        .onErrorComplete()

    private fun notifyAllEmitters(
        pageNumber: Int,
        getTextResult: GetTextResult,
        emitters: List<SingleEmitter<GetTextResult>>
    ): Completable = Completable.fromCallable {
        emitters.forEachIndexed { index, singleEmitter ->
            Log.d(
                LOG_TAG,
                "Notifying emitter $index for #$pageNumber with result=$getTextResult. isDisposed=${singleEmitter.isDisposed}"
            )
            singleEmitter.onSuccess(getTextResult)
        }
    }
    //endregion

    companion object {
        private const val LOG_TAG = "$LOG_PREFIX TextRepository"
    }
}