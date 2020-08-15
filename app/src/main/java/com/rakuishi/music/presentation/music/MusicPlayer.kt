package com.rakuishi.music.presentation.music

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MusicPlayer(private val context: Context) : Player.EventListener {

    interface Callback {
        fun onMetadataChanged(metadata: MediaMetadataCompat)
    }

    private var metadataList: List<MediaMetadataCompat> = arrayListOf()
    private var exoPlayer: ExoPlayer = SimpleExoPlayer.Builder(context).build()

    var callback: Callback? = null

    private val dataSourceFactory: DefaultDataSourceFactory by lazy {
        DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName),
            null
        )
    }

    init {
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        exoPlayer.addListener(this)
    }

    fun prepare(metadataList: List<MediaMetadataCompat>) {
        this.metadataList = metadataList

        val mediaSources = ConcatenatingMediaSource()
        for (metadata in metadataList) {
            val path = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)
            val mediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(path))
            mediaSources.addMediaSource(mediaSource)
        }

        exoPlayer.prepare(mediaSources)
    }

    fun play() {
        exoPlayer.playWhenReady = true
    }

    fun pause() {
        exoPlayer.playWhenReady = false
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    fun skipToPrevious() {
        exoPlayer.previous()
        exoPlayer.playWhenReady = true
    }

    fun skipToNext() {
        exoPlayer.next()
        exoPlayer.playWhenReady = true
    }

    fun stop() {
        exoPlayer.stop()
    }

    fun destroy() {
        exoPlayer.release()
    }

    fun getCurrentPosition(): Long {
        return exoPlayer.currentPosition
    }

    fun getCurrentWindowIndex(): Int {
        return exoPlayer.currentWindowIndex
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
        // This callback fires twice if you call skipToPrevious() or skipToNext() manually
        callback?.onMetadataChanged(metadataList[exoPlayer.currentWindowIndex])
    }
}