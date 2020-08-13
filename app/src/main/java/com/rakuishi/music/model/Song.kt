package com.rakuishi.music.model

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat

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

    fun toMediaMetadataCompat(): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, contentUri.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            .build()
    }

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