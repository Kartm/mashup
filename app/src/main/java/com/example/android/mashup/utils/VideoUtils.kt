package com.example.android.mashup.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.android.mashup.Video
import com.example.android.mashup.videoData.video.VideoUri
import java.io.ByteArrayOutputStream
import java.io.IOException

class VideoUtils {
    companion object {
        fun GetVideoDataFromUri(videoUri: VideoUri): Video {
            val thumbnail: Bitmap = convertCompressedByteArrayToBitmap(videoUri.thumbnail)
            return Video(videoUri.uri, thumbnail, videoUri.name, videoUri.length);
        }

        fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray? {
            var baos: ByteArrayOutputStream? = null
            return try {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                baos.toByteArray()
            } finally {
                if (baos != null) {
                    try {
                        baos.close()
                    } catch (e: IOException) {
                        Log.i(
                            "app",
                            "ByteArrayOutputStream was not closed"
                        )
                    }
                }
            }
        }

        private fun convertCompressedByteArrayToBitmap(src: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(src, 0, src.size)
        }
    }
}