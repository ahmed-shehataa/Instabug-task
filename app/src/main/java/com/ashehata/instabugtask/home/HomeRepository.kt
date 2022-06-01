package com.ashehata.instabugtask.home

import android.widget.Toast
import com.ashehata.instabugtask.models.RequestModel
import com.ashehata.instabugtask.models.ResponseModel
import com.ashehata.instabugtask.util.makeApiCall

class HomeRepository {

    fun getResponse(requestModel: RequestModel, onResponse: (ResponseModel) -> Unit) {
        makeApiCall(
            requestModel = requestModel,
            onResponse = onResponse
        )
    }
}