package com.ashehata.instabugtask.util

import com.ashehata.instabugtask.models.HttpErrorType
import com.ashehata.instabugtask.models.RequestModel
import com.ashehata.instabugtask.models.ResponseModel
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
    onSuccess: (ResponseModel) -> Unit,
    onFailure: (ResponseModel) -> Unit,
) {

    val url = URL(requestModel.url);
    val httpURLConnection = url.openConnection() as HttpURLConnection

    // setting the  Request Method Type
    //OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, PATCH
    httpURLConnection.requestMethod = requestModel.requestType.name

    // adding the headers for request
    //httpURLConnection.setRequestProperty("Content-Type", "application/json")
   /* httpURLConnection.setRequestProperty("Connection", "keep-alive")
    //httpURLConnection.setRequestProperty("charset", "utf-8")*/


    try {
        val responseCode = httpURLConnection.responseCode

        val responseErrorType = when(responseCode) {
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

        val input: InputStream = BufferedInputStream(httpURLConnection.inputStream)
        val reader = BufferedReader(InputStreamReader(input))
        val result = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            result.append(line)
        }

        when(responseErrorType) {
            HttpErrorType.None -> {
                // after response code of your request
                onSuccess(
                    ResponseModel(
                        requestModel = requestModel,
                        responseCode = responseCode,
                        responseBody = result.toString(),
                        error = null,
                    )
                )
            }
            else -> {
                onFailure(
                    ResponseModel(
                        requestModel = requestModel,
                        responseCode = httpURLConnection.responseCode,
                        responseBody = httpURLConnection.responseMessage,
                        error = responseErrorType,
                    )
                )
            }
        }


    } catch (e: Exception) {
        onFailure(
            ResponseModel(
                requestModel = requestModel,
                responseCode = httpURLConnection.responseCode,
                responseBody = httpURLConnection.responseMessage,
                // todo get the correct error
                error = HttpErrorType.InternalServerError,
            )
        )
    } finally {
        httpURLConnection.disconnect()
    }


}