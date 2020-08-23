package com.rakuishi.music.presentation.album_list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rakuishi.music.data.MusicRepository
import com.rakuishi.music.model.Album
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlbumListViewModel @ViewModelInject constructor(
    private val musicRepository: MusicRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val albumList = MutableLiveData<List<Album>>()
        .apply { postValue(arrayListOf()) }

    fun retrieveAlbums() {
        GlobalScope.launch {
            albumList.postValue(musicRepository.retrieveAlbums())
        }
    }
}