package com.rakuishi.music.presentation.main

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rakuishi.music.data.MusicRepository

class MainViewModel @ViewModelInject constructor(
    private val musicRepository: MusicRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun retrieveMusic() {
        musicRepository.retrieveAlbums()
    }
}