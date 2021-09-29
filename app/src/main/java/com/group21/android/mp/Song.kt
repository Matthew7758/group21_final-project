package com.group21.android.mp
import kotlinx.serialization.Serializable

data class Song (
    var name: String = "",
    var album: String = "",
    var artist: String = "",
    var path: String = "",
    var duration: String = ""
)
