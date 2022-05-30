package com.ashehata.instabugtask.models

data class Header(
    val key: String = "",
    val value: String = "",
) {
    fun all() = "$key-$value"
}
