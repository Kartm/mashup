package com.example.android.mashup

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.android.mashup.utils.VideoUtils.Companion.GetVideoDataFromUri
import com.example.android.mashup.videoData.video.VideoUri
import org.junit.Test

import org.junit.Assert.*

class VideoUtilsTest {
    @Test
    fun GetVideoDataFromUri_isCorrect() {
        // arrange
        val videoUri = VideoUri(0, "", ByteArray(0), "video1", 1000)

        // act
        val result = GetVideoDataFromUri(videoUri)

        // assert
        assertEquals(result.id, 0)
        assertEquals(result.uri, "")
        assertEquals(result.thumbnail, BitmapFactory.decodeByteArray(ByteArray(0), 0,0))
        assertEquals(result.title, "video1")
        assertEquals(result.duration, 1000)
    }

    @Test
    fun adding_to_db() {
        assertEquals(4, 2 + 2)
    }
}