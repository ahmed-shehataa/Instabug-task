package com.ashehata.instabugtask.models

import java.io.Serializable

data class KeyValue(
    val key: String? = "",
    val value: String? = "",
) : Serializable {
    fun all() = "$key-$value"
}
