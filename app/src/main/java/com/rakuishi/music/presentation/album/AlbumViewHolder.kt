package com.rakuishi.music.presentation.album

import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rakuishi.music.R
import com.rakuishi.music.model.Album
import kotlinx.android.synthetic.main.list_item_album.view.*


class AlbumViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_album, parent, false)
    ) {

    fun bind(album: Album, onClick: ((Album) -> Unit)?, onLongClick: ((Album) -> Unit)?) {
        itemView.selectableLayout.setOnClickListener { onClick?.invoke(album) }
        itemView.selectableLayout.setOnLongClickListener {
            onLongClick?.invoke(album)
            true
        }

        itemView.titleTextView.text = album.title
        itemView.artistTextView.text = album.artist

        val bitmap: Bitmap? = loadThumbnail(album.contentUri)
        if (bitmap != null) itemView.artworkImageView.setImageBitmap(bitmap)
    }

    private fun loadThumbnail(uri: Uri): Bitmap? {
        return try {
            val context = itemView.context
            val dp = context.resources.getDimension(R.dimen.artwork).toInt()
            val size = Size(dp, dp)
            context.contentResolver.loadThumbnail(
                uri,
                size,
                null
            )
        } catch (e: Exception) {
            null
        }
    }
}