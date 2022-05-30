package com.ashehata.instabugtask.util

import android.util.Patterns


fun String.isValidURL(): Boolean {
    return this.isNotEmpty() && Patterns.WEB_URL.matcher(this).matches();
}