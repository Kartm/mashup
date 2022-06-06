package com.example.android.mashup

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.android.mashup.utils.VideoUtils.Companion.GetVideoDataFromUri
import com.example.android.mashup.videoData.video.VideoUri
import org.junit.Test

import org.junit.Assert.*
import androidx.test.core.app.ApplicationProvider
import com.example.android.mashup.videoData.video.VideoUriViewModel

import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.lang.Exception
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4




//@RunWith(MockitoJUnitRunner::class)
@Config(manifest = "src/main/AndroidManifest.xml") // or app/src/main/AndroidManifest.xml
@RunWith(AndroidJUnit4::class)
class VideoUtilsTest {
    private lateinit var context: Application

    @Before
    fun init() {
        context = ApplicationProvider.getApplicationContext()
    }

//    @Test
//    fun GetVideoDataFromUri_isCorrect() {
//        // arrange
//        val videoUri = VideoUri(0, "", ByteArray(0), "video1", 1000)
//
//        // act
//        val result = GetVideoDataFromUri(videoUri)
//
//        // assert
//        assertEquals(result.id, 0)
//        assertEquals(result.uri, "")
//        assertEquals(result.thumbnail, BitmapFactory.decodeByteArray(ByteArray(0), 0,0))
//        assertEquals(result.title, "video1")
//        assertEquals(result.duration, 1000)
//    }

    @Test
    fun adding_to_db() {
        Log.d("test", context.toString())

        val videoUriVm = VideoUriViewModel(context!!)

        Log.d("test", videoUriVm.toString())
        Log.d("test", videoUriVm.readAllData.toString())
        Log.d("test", videoUriVm.readAllData.value.toString())
        Log.d("test", videoUriVm.readAllData.value!!.size.toString())

        assertEquals(videoUriVm.readAllData.value!!.size, 0)
    }
}