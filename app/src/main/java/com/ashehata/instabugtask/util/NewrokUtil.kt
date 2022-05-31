package com.ashehata.instabugtask.util

import android.util.Log
import com.ashehata.instabugtask.models.*
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

/**
 *
 */
fun makeApiCall(
    requestModel: RequestModel,
    onResponse: (ResponseModel) -> Unit,
) {
    var httpURLConnection: HttpURLConnection? = null
    try {
        var myUrl = requestModel.url
        if (requestModel.requestType == RequestType.GET) {
            if (requestModel.queryParameters.isNotEmpty()) {
                val query = StringBuilder()
                query.append("?")
                requestModel.headers.forEach {
                    query.append(it.key + "=" + it.value)
                }
                myUrl = requestModel.url + query.toString()
            }
        }

        val url = URL(myUrl)
        httpURLConnection = url.openConnection() as HttpURLConnection

        // setting the  Request Method Type
        //OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, PATCH
        httpURLConnection.requestMethod = requestModel.requestType.name

        requestModel.headers.forEach {
            httpURLConnection.setRequestProperty(it.key, it.value)
        }

        val responseCode = httpURLConnection.responseCode
        Log.i("makeApiCall: ", responseCode.toString())

        val responseErrorType = when (responseCode) {
            200 -> HttpErrorType.None
            400 -> HttpErrorType.BadRequest
            401 -> HttpErrorType.NotAuthorized
            403 -> HttpErrorType.Forbidden
            404 -> HttpErrorType.NotFound
            422 -> HttpErrorType.DataInvalid
            500 -> HttpErrorType.InternalServerError
            502 -> HttpErrorType.BadGateway
            else -> HttpErrorType.Unknown
        }
        Log.i("makeApiCall: size", httpURLConnection.headerFields.size.toString())
        val responseHeaders = getResponseHeaders(httpURLConnection.headerFields)


        val input: InputStream = BufferedInputStream(httpURLConnection.inputStream)
        val reader = BufferedReader(InputStreamReader(input))
        val result = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            result.append(line)
        }

        val finalRes = result.toString().replace("\\", "")
        when (responseErrorType) {
            HttpErrorType.None -> {
                // after response code of your request
                onResponse(
                    ResponseModel(
                        headers = responseHeaders,
                        requestModel = requestModel,
                        responseCode = responseCode,
                        responseBody = finalRes + finalRes + finalRes,
                        error = HttpErrorType.None,
                    )
                )
            }
            else -> {
                onResponse(
                    ResponseModel(
                        headers = responseHeaders,
                        requestModel = requestModel,
                        responseCode = httpURLConnection.responseCode,
                        responseBody = result.toString(),
                        error = responseErrorType,
                    )
                )
            }
        }


    } catch (e: Exception) {

        onResponse(
            ResponseModel(
                //headers = responseHeaders,
                requestModel = requestModel,
                responseCode = httpURLConnection?.responseCode ?: -1,
                responseBody = httpURLConnection?.responseMessage.toString(),
                // todo get the correct error
                error = HttpErrorType.InternalServerError,
                errorMessage = e.message
            )
        )
    } finally {
        httpURLConnection?.disconnect()
    }


}

fun getResponseHeaders(headerFields: Map<String, MutableList<String>>): List<KeyValue> {
    return headerFields.map {
        return@map KeyValue(it.key ?: "", it.value.first())
    }.toList()
}
