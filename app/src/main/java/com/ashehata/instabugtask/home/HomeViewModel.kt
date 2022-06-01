package com.ashehata.instabugtask.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.ashehata.instabugtask.custom.RequestTypeException
import com.ashehata.instabugtask.custom.URLFieldException
import com.ashehata.instabugtask.models.ResponseModel
import com.ashehata.instabugtask.singelton.Singleton
import com.ashehata.instabugtask.util.LogMe
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HomeViewModel : ViewModel() {

    private var homeUseCase: HomeUseCase = Singleton.getHomeUseCase()

    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    /**
     * state channel
     */
    private val _stateLiveData: MutableLiveData<HomeViewState> = MutableLiveData(
        HomeViewState()
    )
    val stateLiveData: LiveData<HomeViewState> = _stateLiveData

    /**
     * intent channel
     */
    val intentLiveData: MutableLiveData<HomeIntent> = MutableLiveData()

    private val intentObserver = Observer<HomeIntent> {
        // try to get the intent
        when (it) {
            HomeIntent.Clear -> clearState()
            is HomeIntent.GetResponse -> getResponse(it)
        }
    }


    init {
        intentLiveData.observeForever(intentObserver)
    }


    private fun getResponse(it: HomeIntent.GetResponse) {
        emitLoadingState()
        executor.execute {
            try {
                homeUseCase.getResponse(it.requestModel, onResponse = {
                    emitResponse(it)
                })
            } catch (url: URLFieldException) {
                emitUrlNotValid()
            } catch (requestType: RequestTypeException) {
                emitRequestTypeNotValid()
            }
        }

    }

    private fun emitResponse(it: ResponseModel) {
        _stateLiveData.postValue(
            stateLiveData.value?.copy(
                responseModel = it,
                isSuccess = true,
                isLoading = false
            )
        )
    }

    private fun stopLoadingState() {
        _stateLiveData.postValue(
            stateLiveData.value?.copy(
                isLoading = false,
            )
        )
    }

    private fun emitLoadingState() {
        _stateLiveData.value = stateLiveData.value?.copy(
            isLoading = true,
            isUrlValid = true,
            isRequestTypeValid = true,
        )
    }

    private fun emitRequestTypeNotValid() {
        _stateLiveData.postValue(
            stateLiveData.value?.copy(
                isSuccess = false,
                isLoading = false,
                responseModel = null,
                isUrlValid = true,
                isRequestTypeValid = false,
            )
        )
    }

    private fun emitUrlNotValid() {
        _stateLiveData.postValue(
            stateLiveData.value?.copy(
                isSuccess = false,
                isLoading = false,
                responseModel = null,
                isUrlValid = false,
            )
        )
    }

    private fun clearState() {
        _stateLiveData.value = stateLiveData.value?.copy(
            isSuccess = false,
            isLoading = false,
            responseModel = null,
        )
    }


    override fun onCleared() {
        super.onCleared()
        LogMe.i("onCleared", "")
        // prevent memory leaks !!
        _stateLiveData.value = HomeViewState()
        intentLiveData.removeObserver(intentObserver)
        if (!executor.isShutdown) {
            executor.shutdown()
        }

    }
}