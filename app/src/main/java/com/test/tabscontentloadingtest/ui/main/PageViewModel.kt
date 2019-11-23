package com.test.tabscontentloadingtest.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.tabscontentloadingtest.di.InstanceProvider
import com.test.tabscontentloadingtest.domain.Constants.LOG_PREFIX
import com.test.tabscontentloadingtest.domain.entity.GetTextResult
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class PageViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val contentLoadingRequestSubject = PublishSubject.create<Unit>()

    private val pageInteractor = InstanceProvider.pageInteractor

    val getTextResult = MutableLiveData<GetTextResult>()
    val isLoadingInProgress = MutableLiveData<Boolean>()

    var pageNumber: Int = 0
        set(value) {
            field = value
            Log.d(LOG_TAG, "Initialized: #$pageNumber")
        }

    init {
        val disposable = contentLoadingRequestSubject
            .doOnNext { Log.d(LOG_TAG, "#$pageNumber: Loading requested") }
            .switchMapSingle { startContentLoading() }
            .subscribe({ getTextResult ->
                this.getTextResult.postValue(getTextResult)
                Log.d(LOG_TAG, "#$pageNumber: Loaded: $getTextResult")
            }, {
                Log.e(LOG_TAG, "#$pageNumber: Loading error: ", it)
            })

        compositeDisposable.add(disposable)
    }

    fun requestContentLoading() {
        contentLoadingRequestSubject.onNext(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        Log.d(LOG_TAG, "Cleared: #$pageNumber")
    }

    private fun startContentLoading(): Single<GetTextResult> = pageInteractor
        .getText(pageNumber)
        .doOnSubscribe { isLoadingInProgress.postValue(true) }
        .doOnSuccess { isLoadingInProgress.postValue(false) }
        .doOnError { isLoadingInProgress.postValue(false) }

    companion object {
        private const val LOG_TAG = "$LOG_PREFIX PageViewModel"
    }
}
