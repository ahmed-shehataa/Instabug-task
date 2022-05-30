package com.ashehata.instabugtask.models

data class ResponseModel(
    /**
     * Response data
     */
    val responseCode: Int = -1,
    val responseBody: String = "",
    val error: HttpErrorType? = null,
    val headers: List<KeyValue> = emptyList(),
    /**
     * Request data
     */
    val requestModel: RequestModel? = null,
)
