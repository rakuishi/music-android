package com.rakuishi.music.presentation.album_detail

import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rakuishi.music.R
import com.rakuishi.music.util.*
import kotlinx.android.synthetic.main.list_item_media_metadata.view.*

class MediaMetadataViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_media_metadata, parent, false)
    ) {

    fun bind(metadata: MediaMetadataCompat, onClick: ((MediaMetadataCompat) -> Unit)?) {
        itemView.selectableLayout.setOnClickListener { onClick?.invoke(metadata) }
        itemView.titleTextView.text = metadata.title
        itemView.artistTextView.text = metadata.artist.replaceIfUnknownArtist(itemView.context)
        itemView.durationTextView.text = metadata.duration.getMSSFormat()
    }
}