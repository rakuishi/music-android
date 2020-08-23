package com.rakuishi.music.util

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat

val MediaMetadataCompat.id: String
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

val MediaMetadataCompat.mediaUri: Uri?
    get() {
        val uriString = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)
        return if (uriString != null) Uri.parse(uriString) else null
    }

val MediaMetadataCompat.title: String
    get() = getString(MediaMetadataCompat.METADATA_KEY_TITLE)

val MediaMetadataCompat.album: String
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM)

val MediaMetadataCompat.artist: String
    get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

val MediaMetadataCompat.duration: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

val MediaMetadataCompat.year: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_YEAR)

val MediaMetadataCompat.trackNumber: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)

val MediaMetadataCompat.discNumber: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER)

val MediaMetadataCompat.isNotEmpty: Boolean
    get() = id.isNotBlank() && mediaUri != null && duration > 0L