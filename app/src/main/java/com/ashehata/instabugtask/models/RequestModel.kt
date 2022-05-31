package com.ashehata.instabugtask.models

import java.io.Serializable

data class RequestModel(
    /**
     * Request data
     */
    val url: String = "",
    val requestType: RequestType = RequestType.NONE,
    val headers: List<KeyValue> = emptyList(),
    val queryParameters: List<KeyValue> = emptyList(),
    val requestBody: String = "",
) : Serializable
