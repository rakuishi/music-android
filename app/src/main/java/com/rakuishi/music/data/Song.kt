package com.rakuishi.music.data

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore

class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long
) {

    val contentUri: Uri =
        ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )

    override fun toString(): String {
        return "%d: %s, %s, %d, %s".format(
            id,
            title,
            artist,
            duration,
            contentUri.toString()
        )
    }
}