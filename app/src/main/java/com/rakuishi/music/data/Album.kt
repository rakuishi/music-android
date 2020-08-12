package com.rakuishi.music.data

class Album(
    val id: Long,
    val title: String,
    val artist: String
) {

    override fun toString(): String {
        return "%d: %s, %s".format(
            id,
            title,
            artist
        )
    }
}