package com.rakuishi.music.data

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.rakuishi.music.model.Album
import com.rakuishi.music.model.Song
import timber.log.Timber

class MusicRepository(private val context: Context) {

    fun retrieveAlbums(): List<Album> {
        val resolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = resolver.query(uri, null, null, null, null)
        val albums: ArrayList<Album> = arrayListOf()

        when {
            cursor == null -> {
                // query failed, handle error.
            }
            !cursor.moveToFirst() -> {
                // no media on the device
            }
            else -> {
                val idColumn: Int =
                    cursor.getColumnIndex(MediaStore.Audio.Albums._ID)
                val titleColumn: Int =
                    cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
                val artistColumn: Int =
                    cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)

                do {
                    val album = Album(
                        cursor.getLong(idColumn),
                        cursor.getString(titleColumn),
                        cursor.getString(artistColumn)
                    )
                    albums.add(album)
                    Timber.d(album.toString())
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()

        return albums
    }

    fun retrieveSongs(albumId: Long): List<Song> {
        val resolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "%s == %d".format(MediaStore.Audio.Media.ALBUM_ID, albumId)
        val cursor: Cursor? = resolver.query(uri, null, selection, null, null)
        val songs: ArrayList<Song> = arrayListOf()

        when {
            cursor == null -> {
                // query failed, handle error.
            }
            !cursor.moveToFirst() -> {
                // no media on the device
            }
            else -> {
                val idColumn: Int =
                    cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val titleColumn: Int =
                    cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artistColumn: Int =
                    cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val durationColumn: Int =
                    cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)

                do {
                    val song = Song(
                        cursor.getLong(idColumn),
                        cursor.getString(titleColumn),
                        cursor.getString(artistColumn),
                        cursor.getLong(durationColumn)
                    )
                    songs.add(song)
                    Timber.d(song.toString())
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()

        return songs
    }
}