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

    var onSkipPrev: (() -> Unit)? = null
    var onSkipNext: (() -> Unit)? = null
    var onPause: (() -> Unit)? = null
    var onResume: (() -> Unit)? = null

    private var isPlaying: Boolean = false

    init {
        View.inflate(context, R.layout.view_music_player, this)

        skipPrevImageView.isEnabled = false
        playImageView.isEnabled = false
        skipNextImageView.isEnabled = false

        skipPrevImageView.setOnClickListener {
            onSkipPrev?.invoke()
        }

        playImageView.setOnClickListener {
            if (isPlaying) {
                pause()
                onPause?.invoke()
            } else {
                resume()
                onResume?.invoke()
            }
        }

        skipNextImageView.setOnClickListener {
            onSkipNext?.invoke()
        }
    }

    fun resume() {
        isPlaying = true
        playImageView.setImageResource(R.drawable.ic_round_pause_24)

        skipPrevImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorForeground))
        playImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorForeground))
        skipNextImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorForeground))

        skipPrevImageView.isEnabled = true
        playImageView.isEnabled = true
        skipNextImageView.isEnabled = true
    }

    fun pause() {
        isPlaying = false
        playImageView.setImageResource(R.drawable.ic_round_play_arrow_24)
    }
}