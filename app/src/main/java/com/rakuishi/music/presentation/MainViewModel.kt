package com.rakuishi.music.presentation

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.os.bundleOf
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.rakuishi.music.presentation.music.MusicPlayerService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Singleton
class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @Suppress("PrivatePropertyName")
    private val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, "")
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "")
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "")
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "")
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
        .build()

    @Suppress("PrivatePropertyName")
    private val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()

    private val mediaBrowser: MediaBrowserCompat = MediaBrowserCompat(
        context,
        ComponentName(context, MusicPlayerService::class.java),
        MediaBrowserConnectionCallback(),
        null
    ).apply {
        connect()
    }

    private lateinit var mediaController: MediaControllerCompat
    private val subscriptionCallback = MediaBrowserSubscriptionCallback()
    private val controllerCallback = MediaControllerCallback()

    private val mediaMetadata = MutableLiveData<MediaMetadataCompat>()
        .apply { value = NOTHING_PLAYING }
    private val playbackState = MutableLiveData<PlaybackStateCompat>()
        .apply { value = EMPTY_PLAYBACK_STATE }

    val isConnected = MutableLiveData<Boolean>()
        .apply { postValue(false) }
    val mediaState = MediatorLiveData<Pair<MediaMetadataCompat, PlaybackStateCompat>>().apply {
        val observer = Observer<Any?> {
            this.value = Pair(mediaMetadata.value!!, playbackState.value!!)
        }
        this.addSource(playbackState, observer)
        this.addSource(mediaMetadata, observer)
    }

    override fun onCleared() {
        super.onCleared()
        mediaController.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    private inner class MediaBrowserConnectionCallback : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(
                context,
                mediaBrowser.sessionToken
            ).apply {
                registerCallback(controllerCallback)
            }

            mediaBrowser.subscribe(mediaBrowser.root, subscriptionCallback)

            isConnected.postValue(true)
            if (mediaController.metadata != null) mediaMetadata.postValue(mediaController.metadata)
            if (mediaController.playbackState != null) playbackState.postValue(mediaController.playbackState)
        }

        override fun onConnectionSuspended() {
            isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            isConnected.postValue(false)
        }
    }

    private inner class MediaBrowserSubscriptionCallback :
        MediaBrowserCompat.SubscriptionCallback() {
        /* do nothing */
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            this@MainViewModel.mediaMetadata.postValue(metadata)
        }
    }

    fun requestCurrentMediaState() {
        if (isConnected.value == true) {
            mediaController.metadata?.let { mediaMetadata.postValue(it) }
            mediaController.playbackState?.let { playbackState.postValue(it) }
        }
    }

    fun play() {
        mediaController.transportControls.play()
    }

    fun play(albumId: String, mediaMetadataId: String? = null) {
        val bundle = bundleOf("mediaMetadataId" to mediaMetadataId)
        mediaController.transportControls.prepareFromMediaId(albumId, bundle)
        mediaController.transportControls.play()
    }

    fun pause() {
        mediaController.transportControls.pause()
    }

    fun skipToPrevious() {
        mediaController.transportControls.skipToPrevious()
    }

    fun skipToNext() {
        mediaController.transportControls.skipToNext()
    }

    fun seekTo(pos: Long) {
        mediaController.transportControls.seekTo(pos)
    }
}