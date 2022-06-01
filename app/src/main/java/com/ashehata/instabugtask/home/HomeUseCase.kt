package com.ashehata.instabugtask.home

import com.ashehata.instabugtask.custom.RequestTypeException
import com.ashehata.instabugtask.custom.URLFieldException
import com.ashehata.instabugtask.models.RequestModel
import com.ashehata.instabugtask.models.RequestType
import com.ashehata.instabugtask.models.ResponseModel
import com.ashehata.instabugtask.util.LogMe
import com.ashehata.instabugtask.util.isValidURL

class HomeUseCase(private val homeRepository: HomeRepository) {

    fun getResponse(
        requestModel: RequestModel,
        onResponse: (ResponseModel) -> Unit = {}
    ) {
        if (!requestModel.url.isValidURL()) {
            LogMe.i("isValidURL", "")
            throw URLFieldException()
        }

        if (requestModel.requestType == RequestType.NONE) {
            throw RequestTypeException()
        }

        getOurResponse(requestModel, onResponse)
    }

    private fun getOurResponse(
        requestModel: RequestModel,
        onResponse: (ResponseModel) -> Unit = {}
    ) {
        homeRepository.getResponse(requestModel, onResponse)
    }
}