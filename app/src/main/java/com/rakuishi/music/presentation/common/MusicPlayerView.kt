package com.rakuishi.music.presentation.common

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.rakuishi.music.R
import com.rakuishi.music.util.*
import kotlinx.android.synthetic.main.view_music_player.view.*
import java.util.*

class MusicPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    var onSkipToPrevious: (() -> Unit)? = null
    var onSkipToNext: (() -> Unit)? = null
    var onPause: (() -> Unit)? = null
    var onPlay: (() -> Unit)? = null
    var onSeekTo: ((Long) -> Unit)? = null

    private var isPlaying: Boolean = false
    private var mediaMetadata: MediaMetadataCompat? = null
    private var playbackState: PlaybackStateCompat? = null
    private var position: Long = 0
    private var positionTimer: Timer? = null
    private val positionHandler: Handler = Handler(Looper.getMainLooper())

    init {
        View.inflate(context, R.layout.view_music_player, this)

        setMusicPlayerEnabled(false)

        slider.apply {
            stepSize = 1f
            setLabelFormatter { (it * 1000L).toLong().getMSSFormat() }

            addOnChangeListener { _, value, fromUser ->
                if (fromUser) {
                    onSeekTo?.invoke((value * 1000L).toLong()) // seconds -> milliseconds
                }
            }
        }

        skipToPrevImageView.setOnClickListener {
            onSkipToPrevious?.invoke()
        }

        playImageView.setOnClickListener {
            if (isPlaying) {
                pause()
                onPause?.invoke()
            } else {
                play()
                onPlay?.invoke()
            }
        }

        skipToNextImageView.setOnClickListener {
            onSkipToNext?.invoke()
        }
    }

    fun update(mediaMetadata: MediaMetadataCompat, playbackState: PlaybackStateCompat) {
        this.mediaMetadata = mediaMetadata
        this.playbackState = playbackState
        this.position = playbackState.position

        when (playbackState.state) {
            PlaybackStateCompat.STATE_PLAYING,
            PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS,
            PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                play()
            }
            PlaybackStateCompat.STATE_STOPPED -> {
                stop()
            }
            else -> {
                pause()
            }
        }

    }

    private fun play() {
        isPlaying = true
        playImageView.setImageResource(R.drawable.ic_music_player_pause_48)
        setMusicPlayerEnabled(mediaMetadata?.isNotEmpty == true)
        updateMetadataLayout()
        updateSlider()
        updatePositionTextView()
        schedulePositionTimer()
    }

    private fun pause() {
        isPlaying = false
        playImageView.setImageResource(R.drawable.ic_music_player_play_48)
        setMusicPlayerEnabled(mediaMetadata?.isNotEmpty == true)
        updateMetadataLayout()
        updateSlider()
        updatePositionTextView()
        cancelPositionTimer()
    }

    private fun stop() {
        isPlaying = false
        playImageView.setImageResource(R.drawable.ic_music_player_play_48)
        setMusicPlayerEnabled(false)
        updateMetadataLayout()
        updateSlider()
        updatePositionTextView()
        cancelPositionTimer()
    }

    private fun setMusicPlayerEnabled(isEnabled: Boolean) {
        val colorFilter =
            if (isEnabled) ContextCompat.getColor(context, R.color.colorForeground)
            else ContextCompat.getColor(context, R.color.colorForegroundDisabled)

        val playDrawableResId =
            if (isPlaying) R.drawable.ic_music_player_pause_48
            else R.drawable.ic_music_player_play_48
        playImageView.setImageResource(playDrawableResId)

        skipToPrevImageView.setColorFilter(colorFilter)
        playImageView.setColorFilter(colorFilter)
        skipToNextImageView.setColorFilter(colorFilter)

        slider.isEnabled = isEnabled
        skipToPrevImageView.isEnabled = isEnabled
        playImageView.isEnabled = isEnabled
        skipToNextImageView.isEnabled = isEnabled
    }

    private fun updateMetadataLayout() {
        val isNotStopped = playbackState?.state != PlaybackStateCompat.STATE_STOPPED

        if (mediaMetadata?.isNotEmpty == true && isNotStopped) {
            metadataLayout.visibility = View.VISIBLE
            artworkImageView.apply {
                val bitmap = mediaMetadata?.mediaUri?.loadThumbnail(
                    context,
                    R.dimen.artwork
                )
                setImageBitmap(bitmap)
            }
            titleTextView.text = mediaMetadata?.title
            albumTextView.text = mediaMetadata?.album
            artistTextView.text = mediaMetadata?.artist?.replaceIfUnknownArtist(context)
        } else {
            metadataLayout.visibility = View.GONE
        }
    }

    private fun updateSlider() {
        val duration = mediaMetadata?.duration ?: 0
        val isNotStopped = playbackState?.state != PlaybackStateCompat.STATE_STOPPED

        if (duration > 0L && duration > position && isNotStopped) {
            slider.valueTo = (duration / 1000).toFloat()
            slider.value = (position / 1000).toFloat()
        } else {
            slider.value = 0f
            slider.valueTo = 1f
        }
    }

    private fun updatePositionTextView() {
        val duration = mediaMetadata?.duration ?: 0
        val isNotStopped = playbackState?.state != PlaybackStateCompat.STATE_STOPPED

        if (duration > 0L && isNotStopped) {
            currentPositionTextView.text = position.getMSSFormat()
            remainingPositionTextView.text = (duration - position).getMSSFormat()
        } else {
            currentPositionTextView.text = context.getString(R.string.default_mss)
            remainingPositionTextView.text = context.getString(R.string.default_mss)
        }
    }

    private fun schedulePositionTimer() {
        cancelPositionTimer()

        positionTimer = Timer().apply {
            this.schedule(object : TimerTask() {
                override fun run() {
                    positionHandler.post {
                        position += 1000L
                        updateSlider()
                        updatePositionTextView()
                    }
                }
            }, 1000, 1000)
        }
    }

    private fun cancelPositionTimer() {
        positionTimer?.cancel()
        positionTimer = null
    }
}