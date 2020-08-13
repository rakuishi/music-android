package com.rakuishi.music.presentation

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rakuishi.music.R
import com.rakuishi.music.presentation.album.AlbumListFragment
import com.rakuishi.music.presentation.music.MusicPlayerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 1000
    }

    private lateinit var mediaBrowser: MediaBrowserCompat

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            /* do nothing */
        }
    }

    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            MediaControllerCompat.setMediaController(
                this@MainActivity,
                MediaControllerCompat(this@MainActivity, mediaBrowser.sessionToken)
            )
            bindMediaController()
            mediaBrowser.subscribe(mediaBrowser.root, subscriptionCallback)
        }
    }

    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            when (state?.state) {
                PlaybackStateCompat.STATE_PLAYING,
                PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS,
                PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                    musicPlayerView.play()
                }
                else -> {
                    musicPlayerView.pause()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaBrowser = MediaBrowserCompat(
            this,
            ComponentName(this, MusicPlayerService::class.java),
            connectionCallback,
            null
        )

        if (savedInstanceState == null) {
            if (hasReadPermission()) {
                replaceFragment()
            } else {
                requestPermission()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this).unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    private fun replaceFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AlbumListFragment.newInstance())
            .commitNow()
    }

    private fun bindMediaController() {
        val mediaController = MediaControllerCompat.getMediaController(this)
        musicPlayerView.apply {
            onSkipToPrevious = { mediaController.transportControls.skipToPrevious() }
            onSkipToNext = { mediaController.transportControls.skipToNext() }
            onPlay = { mediaController.transportControls.play() }
            onPause = { mediaController.transportControls.pause() }
        }
        mediaController.registerCallback(controllerCallback)
    }

    fun play(albumId: Long) {
        val controls = MediaControllerCompat.getMediaController(this)
            .transportControls
        controls.prepareFromMediaId(albumId.toString(), null)
        controls.play()
    }

    // region Permission

    private fun hasReadPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            replaceFragment()
        } else {
            Timber.d("onRequestPermissionsResult: failure")
        }
    }

    // endregion
}