package com.rakuishi.music.presentation.music

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.rakuishi.music.data.MusicRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice
@AndroidEntryPoint
class MusicPlayerService : MediaBrowserServiceCompat(), MusicPlayer.Callback {

    companion object {
        const val ALBUM_ID = "album_id"
        private const val MEDIA_ROOT_ID = "media_root_id"
        private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
        private const val LOG_TAG = "MusicPlayerService"
    }

    @Inject
    lateinit var musicRepository: MusicRepository

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var musicPlayer: MusicPlayer
    private var metadataList: List<MediaMetadataCompat> = arrayListOf()
    private var albumId: Long = -1

    @PlaybackStateCompat.State
    private var mediaState: Int = PlaybackStateCompat.STATE_NONE

    override fun onCreate() {
        super.onCreate()

        musicPlayer = MusicPlayer(baseContext)
        musicPlayer.callback = this

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
            setPlaybackState(stateBuilder.build())
            setCallback(mediaSessionCallback)
            setSessionToken(sessionToken)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (clientPackageName.contains(baseContext.packageName)) {
            BrowserRoot(MEDIA_ROOT_ID, null)
        } else {
            BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
        }
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (EMPTY_MEDIA_ROOT_ID == parentId) {
            result.sendResult(null)
            return
        }

        result.sendResult(
            metadataList
                .map {
                    MediaBrowserCompat.MediaItem(
                        it.description,
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                    )
                }
                .toMutableList()
        )
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
            setNewState(PlaybackStateCompat.STATE_PAUSED)
            albumId = mediaId?.toLong() ?: -1
            metadataList = musicRepository.retrieveSongs(albumId).map { it.toMediaMetadataCompat() }
            musicPlayer.prepare(metadataList)

            notifyChildrenChanged(MEDIA_ROOT_ID)
        }

        override fun onPrepare() {
            setNewState(PlaybackStateCompat.STATE_PAUSED)
            musicPlayer.prepare(metadataList)
        }

        override fun onPlay() {
            setNewState(PlaybackStateCompat.STATE_PLAYING)
            mediaSession.isActive = true
            mediaSession.setMetadata(metadataList[musicPlayer.currentWindowIndex])
            musicPlayer.play()
        }

        override fun onPause() {
            setNewState(PlaybackStateCompat.STATE_PAUSED)
            musicPlayer.pause()
        }

        override fun onSkipToPrevious() {
            setNewState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS)
            musicPlayer.skipToPrevious()
        }

        override fun onSkipToNext() {
            setNewState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
            musicPlayer.skipToNext()
        }

        override fun onStop() {
            setNewState(PlaybackStateCompat.STATE_STOPPED)
            mediaSession.isActive = false
            musicPlayer.stop()
            stopSelf()
        }
    }

    private fun setNewState(@PlaybackStateCompat.State newState: Int) {
        mediaState = newState
        stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder
            .setActions(getAvailableActions())
            .setState(newState, musicPlayer.currentPosition, 1.0f)
        mediaSession.setPlaybackState(stateBuilder.build())
    }

    @PlaybackStateCompat.Actions
    private fun getAvailableActions(): Long {
        var actions = (
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                        or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
        actions = when (mediaState) {
            PlaybackStateCompat.STATE_STOPPED -> actions or (
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PAUSE
                    )
            PlaybackStateCompat.STATE_PLAYING -> actions or (
                    PlaybackStateCompat.ACTION_STOP
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_SEEK_TO
                    )
            PlaybackStateCompat.STATE_PAUSED -> actions or (
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_STOP
                    )
            else -> actions or (
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                            or PlaybackStateCompat.ACTION_STOP
                            or PlaybackStateCompat.ACTION_PAUSE
                    )
        }
        return actions
    }

    // region MusicPlayer.Callback

    override fun onMetadataChanged(metadata: MediaMetadataCompat) {
        mediaSession.setMetadata(metadata)
    }

    // endregion
}