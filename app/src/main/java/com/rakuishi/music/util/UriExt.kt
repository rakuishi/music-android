package com.rakuishi.music.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.annotation.DimenRes

fun Uri.loadThumbnail(context: Context, @DimenRes dimenRes: Int): Bitmap? {
    return try {
        val dp = context.resources.getDimension(dimenRes).toInt()
        val size = Size(dp, dp)
        context.contentResolver.loadThumbnail(this, size, null)
    } catch (e: Exception) {
        null
    }
}