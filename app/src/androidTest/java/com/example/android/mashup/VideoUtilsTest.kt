package com.example.android.mashup

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.junit.Test
import com.example.android.mashup.utils.VideoUtils;
import com.example.android.mashup.videoData.video.VideoUri
import org.junit.Assert.*

class VideoUtilsTest {
    @Test fun GetVideoDataFromUri_test() {
        val imageBytes = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVQYV2NgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII="
        val byteArray: ByteArray = Base64.decode(imageBytes, Base64.DEFAULT)

        val videoUri = VideoUri(id=0, length=1000, name="My video", thumbnail = byteArray, uri="sample_uri")

        val createdVideoData = VideoUtils.GetVideoDataFromUri(videoUri)

        assertEquals(videoUri.id, createdVideoData.id)
        assertEquals(videoUri.length, createdVideoData.duration)
        assertEquals(videoUri.name, createdVideoData.title)
        assertEquals(videoUri.uri, createdVideoData.uri)
    }
}
