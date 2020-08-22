package com.rakuishi.music.presentation.album_detail

import android.support.v4.media.MediaMetadataCompat
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rakuishi.music.model.Album

class AlbumDetailAdapter(
    private val album: Album
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ALBUM_HEADER = 0
        const val VIEW_TYPE_METADATA = 1
    }

    var onAlbumClick: ((Album) -> Unit)? = null
    var onMetadataClick: ((MediaMetadataCompat) -> Unit)? = null
    private val metadataList: ArrayList<MediaMetadataCompat> = arrayListOf()

    fun submitList(metadataList: List<MediaMetadataCompat>) {
        this.metadataList.clear()
        // To avoid 'Inconsistency detected. Invalid view holder adapter positionViewHolder'
        notifyDataSetChanged()

        this.metadataList.addAll(metadataList)
        notifyItemRangeInserted(1, metadataList.size)
    }

    override fun getItemCount(): Int {
        return 1 + metadataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_ALBUM_HEADER
        } else {
            VIEW_TYPE_METADATA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ALBUM_HEADER -> AlbumDetailViewHolder(parent)
            VIEW_TYPE_METADATA -> MediaMetadataViewHolder(parent)
            else -> throw IllegalStateException("onCreateViewHolder: failure $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ALBUM_HEADER -> {
                (holder as AlbumDetailViewHolder).bind(album, onAlbumClick)
            }
            VIEW_TYPE_METADATA -> {
                val metadata = metadataList[position - 1]
                (holder as MediaMetadataViewHolder).bind(metadata, onMetadataClick)
            }
        }
    }
}