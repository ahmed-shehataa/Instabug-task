package com.ashehata.instabugtask.models

import java.io.Serializable

data class ResponseModel(
    /**
     * Response data
     */
    val responseCode: Int = -1,
    val responseBody: String = "",
    val error: HttpErrorType? = null,
    val errorMessage: String? = null,
    val headers: List<KeyValue>? = emptyList(),
    /**
     * Request data
     */
    val requestModel: RequestModel? = null,
) : Serializable
