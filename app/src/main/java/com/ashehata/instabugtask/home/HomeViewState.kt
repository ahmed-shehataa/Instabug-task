package com.ashehata.instabugtask.home

import com.ashehata.instabugtask.models.KeyValue
import com.ashehata.instabugtask.models.RequestType
import com.ashehata.instabugtask.models.ResponseModel

data class HomeViewState(
    /**
     * ui data
     */
    val enteredUrl: String = "",
    val requestType: RequestType = RequestType.NONE,
    val queryParameters: List<KeyValue>? = null,
    val headers: List<KeyValue>? = null,
    val requestBody: String = "",
    val responseModel: ResponseModel? = null,
    /**
     * ui state
     */
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isUrlValid: Boolean? = null,
    val isRequestTypeValid: Boolean? = null,
)
