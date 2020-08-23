package com.rakuishi.music.presentation.album_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rakuishi.music.R
import com.rakuishi.music.model.Album
import com.rakuishi.music.util.loadThumbnail
import com.rakuishi.music.util.replaceIfUnknownArtist
import kotlinx.android.synthetic.main.list_item_album_detail.view.*

class AlbumDetailViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_album_detail, parent, false)
    ) {

    fun bind(album: Album, onClick: ((Album) -> Unit)?) {
        itemView.titleTextView.text = album.title
        itemView.detailTextView.apply {
            val artist = album.artist.replaceIfUnknownArtist(itemView.context)
            text = if (album.year == 0L) artist else "%s・%d".format(artist, album.year)
        }
        itemView.artworkImageView.apply {
            val bitmap = album.contentUri.loadThumbnail(itemView.context, R.dimen.artwork_header)
            setImageBitmap(bitmap)
        }
        itemView.numberOfSongsTextView.text =
            itemView.context.getString(R.string.number_of_songs, album.numberOfSongs)
        itemView.playButton.setOnClickListener { onClick?.invoke(album) }
    }
}