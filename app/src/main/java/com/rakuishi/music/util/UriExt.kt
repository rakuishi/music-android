package com.rakuishi.music.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.annotation.DimenRes
import androidx.core.graphics.drawable.toBitmap
import com.rakuishi.music.R

fun Uri.loadThumbnail(context: Context, @DimenRes dimenRes: Int): Bitmap? {
    val pixel = context.resources.getDimension(dimenRes).toInt()
    return try {
        context.contentResolver.loadThumbnail(this, Size(pixel, pixel), null)
    } catch (e: Exception) {
        context.getDrawable(R.drawable.placeholder_playback)?.toBitmap(pixel, pixel)
    }
}