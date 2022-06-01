package com.ashehata.instabugtask.home

import com.ashehata.instabugtask.models.RequestModel

sealed class HomeIntent {
    data class GetResponse(val requestModel: RequestModel) : HomeIntent()
    object Clear : HomeIntent()
}