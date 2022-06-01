package com.ashehata.instabugtask.util

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
        //Adding query parameters to url
        val myUrl = addQueryToUrl(requestModel.url, requestModel.queryParameters)
        LogMe.i(TAG, "makeApiCall: URL -> $myUrl")

        // open Http connection
        val url = URL(myUrl)
        httpURLConnection = url.openConnection() as HttpURLConnection

        // setting the  Request Method Type
        //OPTIONS, GET, HEAD
        httpURLConnection.requestMethod = requestModel.requestType.name


        // Enable (doOutput) in POST request only to write content to the connection output stream
        if (requestModel.requestType == RequestType.POST) {
            httpURLConnection.doOutput = true
        }

        /*// Add a @PREDEFINED_HEADERS to requestModel headers.
        requestModel.headers = requestModel.headers.toMutableList().apply {
            //addAll(PREDEFINED_HEADERS)
        }*/

        // Add all request headers to the Http Connection
        requestModel.headers.forEach {
            httpURLConnection.setRequestProperty(it.key, it.value)
        }

        // Add all request body to the Http Connection
        if (requestModel.requestBody.isNotEmpty()) {
            httpURLConnection.outputStream.use { os ->
                val input: ByteArray = requestModel.requestBody.toByteArray()
                os.write(input, 0, input.size)
            }
        }

        responseCode = httpURLConnection.responseCode
        LogMe.i("makeApiCall: responseCode", responseCode.toString())

        val responseErrorType = getResponseType(responseCode)

        val responseHeaders = getResponseHeaders(httpURLConnection.headerFields)
        LogMe.i("makeApiCall: size", httpURLConnection.headerFields.size.toString())


        val finalRes = readResponseBody(httpURLConnection.inputStream)

        onResponse(
            ResponseModel(
                headers = responseHeaders,
                requestModel = requestModel,
                responseCode = responseCode,
                responseBody = finalRes,
                error = responseErrorType,
                errorMessage = null
            )
        )
    } catch (e: Exception) {
        onResponse(
            ResponseModel(
                headers = null,
                responseCode = responseCode,
                requestModel = requestModel,
                responseBody = httpURLConnection?.responseMessage.toString(),
                errorMessage = e.toString()
            )
        )
    } finally {
        httpURLConnection?.disconnect()
    }
}

/**
 * This method take responseCode to get the HttpErrorType
 * @param responseCode the response code from HttpUrlConnection
 * @return an HttpErrorType object
 * @see HttpErrorType
 */
private fun getResponseType(responseCode: Int): HttpErrorType {
    return when (responseCode) {
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
}

/**
 * This method take url and add the queryParameters to it
 * @param url is API base url
 * @param queryParameters is List of queryParameters that you want to add for the GET request
 * @return the final url concatenated with queryParameters as String
 * @see RequestType
 * @see KeyValue
 */
private fun addQueryToUrl(url: String, queryParameters: List<KeyValue>): String {
    if (queryParameters.isNotEmpty()) {
        val query = StringBuilder()
        query.append("?")
        queryParameters.forEach {
            query.append(it.key + "=" + it.value)
            query.append("&")
        }
        return url + query.toString()
    }
    return url
}


/**
 * This method to read responseHeaders from Map<String, MutableList<String>>
 * @param headerFields
 * @return List<KeyValue>
 * @see KeyValue
 */
private fun getResponseHeaders(headerFields: Map<String, MutableList<String>>): List<KeyValue> {
    return headerFields.map {
        return@map KeyValue(it.key ?: "", it.value.first())
    }.toList()
}

fun readResponseBody(inputStream: InputStream?): String {
    val input: InputStream = BufferedInputStream(inputStream)
    val reader = BufferedReader(InputStreamReader(input))
    val result = StringBuilder()
    var line: String?

    // Read the response body as string
    while (reader.readLine().also { line = it } != null) {
        result.append(line)
    }

    val finalRes = result.toString().replace("\\", "")
    return finalRes
}

