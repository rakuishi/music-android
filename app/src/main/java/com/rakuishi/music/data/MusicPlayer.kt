package com.rakuishi.music.data

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import timber.log.Timber

class MusicPlayer(private val context: Context) : Player.EventListener {

    private var songs: List<Song> = arrayListOf()
    private var exoPlayer: ExoPlayer = SimpleExoPlayer.Builder(context).build()

    private val dataSourceFactory: DefaultDataSourceFactory by lazy {
        DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "com.rakuishi.music"),
            null
        )
    }

    init {
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        exoPlayer.addListener(this)
    }

    fun start(songs: List<Song>) {
        this.songs = songs

        val mediaSources = ConcatenatingMediaSource()
        for (song in songs) {
            val mediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(song.contentUri)
            mediaSources.addMediaSource(mediaSource)
        }
        exoPlayer.prepare(mediaSources)

        exoPlayer.playWhenReady = true
    }

    fun previous() {
        exoPlayer.previous()
    }

    fun next() {
        exoPlayer.next()
    }

    fun resume() {
        exoPlayer.playWhenReady = true
    }

    fun pause() {
        exoPlayer.playWhenReady = false
    }

    fun destroy() {
        exoPlayer.release()
    }

    fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
        super.onTracksChanged(trackGroups, trackSelections)
        Timber.d("onTracksChanged: %s", songs[exoPlayer.currentWindowIndex].toString())
    }
}