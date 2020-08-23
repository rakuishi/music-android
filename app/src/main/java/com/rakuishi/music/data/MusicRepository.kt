package com.rakuishi.music.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.util.LruCache
import com.rakuishi.music.model.Album
import com.rakuishi.music.util.discNumber
import com.rakuishi.music.util.trackNumber
import kotlin.math.max
import kotlin.math.pow

class MusicRepository(private val context: Context) {

    // Use cache because of taking time to read ContentResolver
    private val metadataListCache: LruCache<Long, List<MediaMetadataCompat>> = LruCache(50)

    fun retrieveAlbums(): List<Album> {
        val resolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val sortOrder = "%s ASC".format(MediaStore.Audio.Albums._ID)
        val cursor: Cursor? = resolver.query(uri, null, null, null, sortOrder)
        val albums: ArrayList<Album> = arrayListOf()

        if (cursor == null || !cursor.moveToFirst()) {
            cursor?.close()
            return albums
        }

        val idColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Albums._ID)
        val titleColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
        val artistColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)
        val numberOfSongsColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
        val yearColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR)

        do {
            val album = Album(
                cursor.getLong(idColumn),
                cursor.getString(titleColumn),
                cursor.getString(artistColumn),
                cursor.getLong(numberOfSongsColumn),
                cursor.getLong(yearColumn)
            )
            albums.add(album)

        } while (cursor.moveToNext())

        cursor.close()

        return albums
    }

    fun retrieveSongs(albumId: Long): List<MediaMetadataCompat> {
        metadataListCache.get(albumId)?.let {
            return it
        }

        val resolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "%s == %d".format(MediaStore.Audio.Media.ALBUM_ID, albumId)
        val cursor: Cursor? = resolver.query(uri, null, selection, null, null)
        val metadataList: ArrayList<MediaMetadataCompat> = arrayListOf()
        val retriever = MediaMetadataRetriever()

        if (cursor == null || !cursor.moveToFirst()) {
            cursor?.close()
            return metadataList
        }

        val idColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val titleColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val albumColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
        val artistColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val durationColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
        val yearColumn: Int =
            cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
        var maxTrackNumber: Long = 1

        do {
            val id = cursor.getLong(idColumn)
            val mediaUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )
            val title = cursor.getString(titleColumn)
            val album = cursor.getString(albumColumn)
            val artist = cursor.getString(artistColumn)
            val duration = cursor.getLong(durationColumn)
            val year = cursor.getLong(yearColumn)

            retriever.setDataSource(context, mediaUri)

            val discNumber =
                parseNumber(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER))
            val trackNumber =
                parseNumber(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER))

            maxTrackNumber = max(maxTrackNumber, trackNumber)

            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaUri.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .putLong(MediaMetadataCompat.METADATA_KEY_YEAR, year)
                .putLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER, discNumber)
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber)
                .build()
            metadataList.add(metadata)

        } while (cursor.moveToNext())

        cursor.close()

        val x = 10f.pow(maxTrackNumber.toString().length)
        metadataList.sortWith(Comparator { a, b ->
            (a.discNumber * x + a.trackNumber).toInt() - (b.discNumber * x + b.trackNumber).toInt()
        })

        metadataListCache.put(albumId, metadataList)
        return metadataList
    }

    private fun parseNumber(number: String?): Long {
        val numbers = number?.split("/") ?: listOf()
        return if (numbers.size == 2) {
            numbers.first().toLong()
        } else {
            1
        }
    }
}