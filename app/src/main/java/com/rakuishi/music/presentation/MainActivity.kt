package com.rakuishi.music.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.rakuishi.music.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (hasReadPermission()) {
            inflateNavigation()
        } else {
            requestPermission()
        }

        subscribeUi()
        bindUi()
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestCurrentMediaState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.findNavController().navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun inflateNavigation() {
        navHostFragment.findNavController().apply {
            graph = navInflater.inflate(R.navigation.navigation_album)
            appBarConfiguration = AppBarConfiguration(graph)
            this@MainActivity.setupActionBarWithNavController(this, appBarConfiguration)
            addOnDestinationChangedListener { _, _, _ ->
                supportActionBar?.elevation = resources.getDimension(R.dimen.actionbar_elevation)
            }
        }
    }

    private fun subscribeUi() {
        viewModel.isConnected.observe(this, Observer {
            Timber.d("isConnected: %s", it)
        })

        viewModel.mediaState.observe(this, Observer {
            Timber.d("mediaState: %s %d", it.first.description?.title, it.second.state)
            musicPlayerView.update(it.first, it.second)
        })
    }

    private fun bindUi() {
        musicPlayerView.apply {
            onSkipToPrevious = { viewModel.skipToPrevious() }
            onSkipToNext = { viewModel.skipToNext() }
            onPlay = { viewModel.play() }
            onPause = { viewModel.pause() }
            onSeekTo = { viewModel.seekTo(it) }
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
        val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                inflateNavigation()
            } else {
                Timber.d("onRequestPermissionsResult: failure")
            }
        }
        launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    // endregion
}