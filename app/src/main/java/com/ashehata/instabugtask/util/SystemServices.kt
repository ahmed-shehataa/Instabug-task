package com.ashehata.instabugtask.util

import android.content.Context
import android.net.ConnectivityManager

fun Context.isNetworkConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
}