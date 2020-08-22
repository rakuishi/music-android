package com.rakuishi.music.presentation.album_detail

import android.support.v4.media.MediaMetadataCompat
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rakuishi.music.data.MusicRepository

class AlbumDetailViewModel @ViewModelInject constructor(
    private val musicRepository: MusicRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val metadataList = MutableLiveData<List<MediaMetadataCompat>>()
        .apply { postValue(arrayListOf()) }

    fun retrieveSongs(albumId: Long) {
        metadataList.postValue(musicRepository.retrieveSongs(albumId))
    }
}