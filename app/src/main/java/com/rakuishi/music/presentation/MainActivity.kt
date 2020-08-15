package com.rakuishi.music.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.rakuishi.music.R
import com.rakuishi.music.presentation.album.AlbumListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 1000
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (hasReadPermission()) {
                replaceFragment()
            } else {
                requestPermission()
            }
        }

        subscribeUi()
        bindUi()
    }

    private fun replaceFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AlbumListFragment.newInstance())
            .commitNow()
    }

    private fun subscribeUi() {
        viewModel.isConnected.observe(this, Observer {
            Timber.d("isConnected: %s", it)
        })

        viewModel.nowPlaying.observe(this, Observer {
            Timber.d("onMetadataChanged: %s", it?.description?.title)
        })

        viewModel.playbackState.observe(this, Observer {
            when (it.state) {
                PlaybackStateCompat.STATE_PLAYING,
                PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS,
                PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                    musicPlayerView.play()
                }
                PlaybackStateCompat.STATE_STOPPED -> {
                    musicPlayerView.stop()
                }
                else -> {
                    musicPlayerView.pause()
                }
            }
        })
    }

    private fun bindUi() {
        musicPlayerView.apply {
            onSkipToPrevious = { viewModel.skipToPrevious() }
            onSkipToNext = { viewModel.skipToNext() }
            onPlay = { viewModel.play() }
            onPause = { viewModel.pause() }
        }
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