package com.rakuishi.music.model

import android.content.ContentUris
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val numberOfSongs: Long
) : Parcelable {

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

    // region Parcelable

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeLong(numberOfSongs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Album> {
        override fun createFromParcel(parcel: Parcel): Album {
            return Album(
                parcel.readLong(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readLong()
            )
        }

        override fun newArray(size: Int): Array<Album?> {
            return arrayOfNulls(size)
        }
    }

    // endregion
}