package com.rakuishi.music.model

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore

data class Album(
    val id: Long,
    val title: String,
    val artist: String
) {

    val contentUri: Uri =
        ContentUris.withAppendedId(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            id
        )

    override fun toString(): String {
        return "%d: %s, %s".format(
            id,
            title,
            artist
        )
    }
}