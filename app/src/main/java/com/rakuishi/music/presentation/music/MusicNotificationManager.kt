package com.rakuishi.music.presentation.music

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.rakuishi.music.R
import com.rakuishi.music.util.loadThumbnail
import com.rakuishi.music.util.replaceIfUnknownArtist

class MusicNotificationManager(
    private val service: MusicPlayerService,
    private val mediaSession: MediaSessionCompat
) {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "com.rakuishi.music.channel.music"
        private const val NOTIFICATION_ID = 1
    }

    private var context: Context = service.baseContext
    private var notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    fun start() {
        service.startService(Intent(context, MusicPlayerService::class.java))
        service.startForeground(NOTIFICATION_ID, buildNotification())
    }

    fun update() {
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    fun stop() {
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
        service.stopForeground(false)
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createNotificationChannelIfNeeded()
        }

        val description = mediaSession.controller.metadata.description

        val builder = NotificationCompat.Builder(
            context,
            NOTIFICATION_CHANNEL_ID
        ).apply {
            setSmallIcon(R.drawable.ic_notification_playback)
            setContentTitle(description.title)
            setSubText(description.description)
            setContentText(description.subtitle.toString().replaceIfUnknownArtist(context))
            setLargeIcon(
                description.mediaUri?.loadThumbnail(
                    context,
                    R.dimen.notification_artwork
                )
            )

            // Make the transport controls visible on the lockscreen
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            // Enable launching the player by clicking the notification
            setContentIntent(mediaSession.controller.sessionActivity)

            // Stop the service when the notification is swiped away
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_notification_skip_previous,
                    context.getString(R.string.skip_to_previous),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )

            val isPlaying =
                mediaSession.controller.playbackState.state == PlaybackStateCompat.STATE_PLAYING
            if (isPlaying) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_notification_pause,
                        context.getString(R.string.pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_PAUSE
                        )
                    )
                )
            } else {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_notification_play,
                        context.getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_PLAY
                        )
                    )
                )
            }

            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_notification_skip_next,
                    context.getString(R.string.skip_to_next),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )

            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context, PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )
        }

        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createNotificationChannelIfNeeded() {
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.playback),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}