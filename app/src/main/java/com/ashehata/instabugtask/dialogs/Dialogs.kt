package com.ashehata.instabugtask.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.ashehata.instabugtask.R
import com.ashehata.instabugtask.models.KeyValue
import java.lang.StringBuilder


fun Context.showHeadersDialog(responseHeaders: List<KeyValue>, requestHeaders: List<KeyValue>?) {
    val dialog = AlertDialog.Builder(this)
    dialog.setTitle(getString(R.string.headers))
    dialog.setMessage(getHeadersOrganized(responseHeaders, requestHeaders))

    dialog.setCancelable(true)

    dialog.setPositiveButton(
        "OK"
    ) { dialog, id ->
        dialog.cancel()
    }


    val alert = dialog.create()
    alert.show()
}


fun Context.showQueryDialog(queries: List<KeyValue>) {
    val dialog = AlertDialog.Builder(this)
    dialog.setTitle(getString(R.string.query_parameters))
    dialog.setMessage(getQueriesOrganized(queries))

    dialog.setCancelable(true)

    dialog.setPositiveButton(
        "OK"
    ) { dialog, id ->
        dialog.cancel()
    }


    val alert = dialog.create()
    alert.show()
}

fun getQueriesOrganized(queries: List<KeyValue>): String {
    val result  = StringBuilder()
    queries.forEach {
        if (it.key.isNullOrEmpty()) {
            result.append(it.value)
        } else {
            result.append("Key: "+it.key + "\n" +"Value: " + it.value)
        }
        result.append("\n ------------------- \n")
    }

    return result.toString()
}

fun getHeadersOrganized(responseHeaders: List<KeyValue>, requestHeaders: List<KeyValue>?): String {
    val result  = StringBuilder()
    result.append("Request Headers: \n \n")

    requestHeaders?.forEach {
        if (it.key.isNullOrEmpty()) {
            result.append(it.value)
        } else {
            result.append("Key: "+it.key + "\n" +"Value: " + it.value)
        }
        result.append("\n ------------------- \n")
    }

    result.append("\n ------********------ \n")

    result.append("Response Headers: \n \n")

    responseHeaders.forEach {
        if (it.key.isNullOrEmpty()) {
            result.append(it.value)
        } else {
            result.append("Key: "+it.key + "\n" +"Value: " + it.value)
        }
        result.append("\n ------------------- \n")
    }
    return result.toString()
}
