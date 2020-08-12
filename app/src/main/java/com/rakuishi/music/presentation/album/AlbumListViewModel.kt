package com.rakuishi.music.presentation.album

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rakuishi.music.data.Album
import com.rakuishi.music.data.MusicRepository
import com.rakuishi.music.data.Song

class AlbumListViewModel @ViewModelInject constructor(
    private val musicRepository: MusicRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun retrieveAlbums(): List<Album> {
        return musicRepository.retrieveAlbums()
    }

    fun retrieveSongs(albumId: Long): List<Song> {
        return musicRepository.retrieveSongs(albumId)
    }
}