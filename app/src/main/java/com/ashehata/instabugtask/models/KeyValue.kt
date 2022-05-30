package com.ashehata.instabugtask.models

data class KeyValue(
    val key: String = "",
    val value: String = "",
) {
    fun all() = "$key-$value"
}
