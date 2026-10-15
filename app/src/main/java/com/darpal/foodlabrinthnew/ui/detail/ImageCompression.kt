package com.darpal.foodlabrinthnew.ui.detail

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

private const val MAX_DIMENSION = 1600
private const val JPEG_QUALITY = 85

suspend fun Bitmap.toJpeg(): ByteArray = withContext(Dispatchers.Default) {
    ByteArrayOutputStream().use { out ->
        compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        out.toByteArray()
    }
}

/** Decodes a gallery image downsampled to roughly [MAX_DIMENSION] px and re-encodes it as JPEG. */
suspend fun ContentResolver.readScaledJpeg(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@withContext null

    var sample = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_DIMENSION) sample *= 2

    val bitmap = openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
    } ?: return@withContext null
    bitmap.toJpeg().also { bitmap.recycle() }
}
