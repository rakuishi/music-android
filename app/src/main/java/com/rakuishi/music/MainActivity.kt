package com.rakuishi.music

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rakuishi.music.data.MusicPlayer
import com.rakuishi.music.data.Song
import com.rakuishi.music.presentation.album.AlbumListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 1000
    }

    @Inject
    lateinit var musicPlayer: MusicPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindView()

        if (savedInstanceState == null) {
            if (hasReadPermission()) {
                replaceFragment()
            } else {
                requestPermission()
            }
        } else {
            if (musicPlayer.isPlaying()) {
                musicPlayerView.resume()
            }
        }
    }

    private fun replaceFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AlbumListFragment.newInstance())
            .commitNow()
    }

    private fun bindView() {
        musicPlayerView.apply {
            onPrevious = { musicPlayer.previous() }
            onResume = { musicPlayer.resume() }
            onPause = { musicPlayer.pause() }
            onNext = { musicPlayer.next() }
        }
    }

    fun play(songs: List<Song>) {
        musicPlayer.start(songs)
        musicPlayerView.resume()
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