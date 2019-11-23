package com.test.tabscontentloadingtest.data.repository

import com.test.tabscontentloadingtest.data.repository.TextUpdateRequestRegistry.PutResult.DOWNLOAD_NEEDED
import com.test.tabscontentloadingtest.data.source.NetworkDataSource
import com.test.tabscontentloadingtest.domain.Constants.LOG_PREFIX
import com.test.tabscontentloadingtest.domain.entity.GetTextResult
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TextNetworkRequestController(
    private val networkDataSource: NetworkDataSource,
    private val callback: Callback
) {

    private val networkRequestCompositeDisposable = CompositeDisposable()
    private val namespaceUpdateRegistry = TextUpdateRequestRegistry()

    fun getText(pageNumber: Int): Single<GetTextResult> = Single.create { emitter ->
        if (namespaceUpdateRegistry.put(pageNumber, emitter) == DOWNLOAD_NEEDED) {
            networkRequestCompositeDisposable.add(startNetworkRequest(pageNumber))
        }
    }

    private fun startNetworkRequest(pageNumber: Int): Disposable = networkDataSource
        .getText(pageNumber)
        .flatMapCompletable { getTextResult ->
            callback.onTextLoaded(pageNumber, getTextResult, namespaceUpdateRegistry.pollRelatedEmitters(pageNumber))
        }
        .subscribeOn(Schedulers.io()) // It's important to switch threads inside Single.create
        .subscribe({}, {
            throw IllegalStateException("Data loading & processing errors should be handled upstream")
        })

    companion object {
        private const val LOG_TAG = "$LOG_PREFIX TextNetworkRequestController"
    }

    interface Callback {

        fun onTextLoaded(
            pageNumber: Int,
            getTextResult: GetTextResult,
            emitters: List<SingleEmitter<GetTextResult>>
        ): Completable
    }
}
