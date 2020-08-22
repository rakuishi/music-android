package com.rakuishi.music.presentation.album_list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rakuishi.music.model.Album
import com.rakuishi.music.data.MusicRepository

class AlbumListViewModel @ViewModelInject constructor(
    private val musicRepository: MusicRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun retrieveAlbums(): List<Album> {
        return musicRepository.retrieveAlbums()
    }
}