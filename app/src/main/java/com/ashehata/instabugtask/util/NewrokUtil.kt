package com.ashehata.instabugtask.util

import android.util.Log
import com.ashehata.instabugtask.models.*
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "makeApiCall"


private val PREDEFINED_HEADERS = listOf(
    KeyValue(key = "Content-Type", value = "application/json"),
    KeyValue(key = "Accept", value = "application/json"),
)

/**
 *
 */
fun makeApiCall(
    requestModel: RequestModel,
    onResponse: (ResponseModel) -> Unit,
) {
    var httpURLConnection: HttpURLConnection? = null
    var responseCode = -1
    try {
        var myUrl = requestModel.url


        if (requestModel.queryParameters.isNotEmpty()) {
            val query = StringBuilder()
            query.append("?")
            requestModel.queryParameters.forEach {
                query.append(it.key + "=" + it.value)
                query.append("&")
            }
            myUrl = requestModel.url + query.toString()
        }
        Log.i(TAG, "makeApiCall: URL ->$myUrl")


        Log.i(TAG, "makeApiCall: URL ->$myUrl")


        val url = URL(myUrl)
        httpURLConnection = url.openConnection() as HttpURLConnection


        // setting the  Request Method Type
        //OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, PATCH
        httpURLConnection.requestMethod = requestModel.requestType.name


        /**
         * To send request content, let's enable the URLConnection object's doOutput property to true.
        Otherwise, we won't be able to write content to the connection output stream:
         */
        if (requestModel.requestType == RequestType.POST) {
            httpURLConnection.doOutput = true
            //httpURLConnection.doInput = true
        }


        requestModel.headers = requestModel.headers.toMutableList().apply {
            addAll(PREDEFINED_HEADERS)
        }


        requestModel.headers.forEach {
            httpURLConnection.setRequestProperty(it.key, it.value)
        }

        if (requestModel.requestBody.isNotEmpty()) {
            httpURLConnection.outputStream.use { os ->
                val input: ByteArray = requestModel.requestBody.toByteArray()
                os.write(input, 0, input.size)
            }
            /* httpURLConnection.setRequestProperty("Content-Length",
                 requestModel.requestBody.length.toString())
             httpURLConnection.getOutputStream().write(requestModel.requestBody.toByteArray())*/
        }

        responseCode = httpURLConnection.responseCode
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
                        errorMessage = null
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
                        errorMessage = null
                    )
                )
            }
        }


    } catch (e: Exception) {

        onResponse(
            ResponseModel(
                //headers = responseHeaders,
                responseCode = responseCode,
                requestModel = requestModel,
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
