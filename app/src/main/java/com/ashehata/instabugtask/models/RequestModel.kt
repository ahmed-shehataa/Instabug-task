package com.ashehata.instabugtask.models

data class RequestModel(
    /**
     * Request data
     */
    val url: String = "",
    val requestType: RequestType = RequestType.NONE,
    val headers: List<KeyValue> = emptyList(),
    val queryParameters: List<KeyValue> = emptyList(),
    val requestBody: String = "",
)
