package com.rakuishi.music.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.rakuishi.music.R
import kotlinx.android.synthetic.main.view_music_player.view.*

class MusicPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    var onSkipToPrevious: (() -> Unit)? = null
    var onSkipToNext: (() -> Unit)? = null
    var onPause: (() -> Unit)? = null
    var onPlay: (() -> Unit)? = null

    private var isPlaying: Boolean = false

    init {
        View.inflate(context, R.layout.view_music_player, this)

        skipToPrevImageView.isEnabled = false
        playImageView.isEnabled = false
        skipToNextImageView.isEnabled = false

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

    fun play() {
        isPlaying = true
        playImageView.setImageResource(R.drawable.ic_round_pause_24)

        skipToPrevImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorForeground))
        playImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorForeground))
        skipToNextImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorForeground))

        skipToPrevImageView.isEnabled = true
        playImageView.isEnabled = true
        skipToNextImageView.isEnabled = true
    }

    fun pause() {
        isPlaying = false
        playImageView.setImageResource(R.drawable.ic_round_play_arrow_24)
    }
}