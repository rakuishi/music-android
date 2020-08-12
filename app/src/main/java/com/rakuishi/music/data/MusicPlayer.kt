package com.rakuishi.music.data

import android.content.Context
import android.media.MediaPlayer

class MusicPlayer(private val context: Context) {

    private var currentMediaPlayer: MediaPlayer
    private var nextMediaPlayer: MediaPlayer
    private var songs: List<Song> = arrayListOf()
    private var currentIndex: Int = 0
    private var isSingleLooping: Boolean = false

    init {
        currentMediaPlayer = MediaPlayer().apply {
            setOnCompletionListener { skipNext() }
        }

        nextMediaPlayer = MediaPlayer().apply {
            setOnCompletionListener { skipNext() }
        }
    }

    fun setDataSource(songs: List<Song>) {
        this.songs = songs
    }

    fun start(index: Int = 0, forceStart: Boolean = true) {
        this.currentIndex = index

        currentMediaPlayer.apply {
            reset()
            isLooping = isSingleLooping
            setDataSource(context, songs[currentIndex].contentUri)
            prepare()
        }

        prepareNextMediaPlayer()
        if (forceStart)
            currentMediaPlayer.start()
    }

    fun skipPrev() {
        val ratio =
            currentMediaPlayer.currentPosition.toFloat() / currentMediaPlayer.duration.toFloat()
        val justSeekToZero = ratio < 0.1

        if (isSingleLooping || justSeekToZero) {
            currentMediaPlayer.seekTo(0)
        } else {
            val isPlaying = currentMediaPlayer.isPlaying
            currentMediaPlayer.stop()

            val prevIndex = if (currentIndex == 0) songs.size - 1 else currentIndex - 1
            start(prevIndex, isPlaying)
        }
    }

    fun skipNext() {
        if (isSingleLooping) {
            currentMediaPlayer.seekTo(0)
        } else {
            val isPlaying = currentMediaPlayer.isPlaying
            currentMediaPlayer.stop()

            val temp = nextMediaPlayer
            nextMediaPlayer = currentMediaPlayer
            currentMediaPlayer = temp

            currentIndex = if (currentIndex == songs.size - 1) 0 else currentIndex + 1
            prepareNextMediaPlayer()

            if (isPlaying)
                currentMediaPlayer.start()
        }
    }

    fun resume() {
        currentMediaPlayer.start()
    }

    fun pause() {
        currentMediaPlayer.pause()
    }

    fun destroy() {
        currentMediaPlayer.release()
        nextMediaPlayer.release()
    }

    fun isPlaying(): Boolean {
        return currentMediaPlayer.isPlaying
    }

    private fun prepareNextMediaPlayer() {
        nextMediaPlayer.apply {
            val nextSong =
                if (currentIndex == songs.size - 1) songs[0] // first song
                else songs[currentIndex + 1] // next song

            reset()
            isLooping = isSingleLooping
            setDataSource(context, nextSong.contentUri)
            prepare()
        }

        currentMediaPlayer.setNextMediaPlayer(nextMediaPlayer)
    }
}