package com.rakuishi.music.util

import kotlin.math.floor

fun Long.getMSSFormat(): String {
    val seconds = floor(toDouble() / 1000).toInt()
    return "%d:%02d".format(seconds / 60, seconds % 60)
}