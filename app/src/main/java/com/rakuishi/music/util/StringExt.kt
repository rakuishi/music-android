package com.rakuishi.music.util

import android.content.Context
import com.rakuishi.music.R

fun String.replaceIfUnknownArtist(context: Context): String {
    return if (this == "<unknown>") {
        context.getString(R.string.unknown_artist)
    } else {
        this
    }
}
