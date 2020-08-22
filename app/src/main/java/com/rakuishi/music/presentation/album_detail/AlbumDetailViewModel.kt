package com.rakuishi.music.presentation.album_detail

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.rakuishi.music.R
import com.rakuishi.music.data.MusicRepository
import dagger.hilt.android.qualifiers.ApplicationContext

class AlbumDetailViewModel @ViewModelInject constructor(
    private val musicRepository: MusicRepository,
    @ApplicationContext private val context: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val metadataList = MutableLiveData<List<MediaMetadataCompat>>()
        .apply { postValue(arrayListOf()) }
    val actionBarElevation = MutableLiveData<Float>()
        .apply { postValue(0f) }

    // TODO: artwork_header_height will be replaced by actual view height
    private val scrollOffset: Float by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimension(R.dimen.artwork_header_height)
    }

    private var totalY = 0

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            totalY += dy

            val elevation =
                if (totalY >= scrollOffset) context.resources.getDimension(R.dimen.actionbar_elevation)
                else 0f
            actionBarElevation.postValue(elevation)
        }
    }

    fun retrieveSongs(albumId: Long) {
        metadataList.postValue(musicRepository.retrieveSongs(albumId))
    }
}