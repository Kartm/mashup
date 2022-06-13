package com.example.android.mashup

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.mashup.utils.AudioVideoMerger
import com.example.android.mashup.utils.FFMpegCallback
import com.simform.videooperations.FFmpegCallBack
import org.jcodec.common.IOUtils
import org.junit.Test
import org.junit.Assert.*
import org.mockito.ArgumentMatchers.anyString
import java.io.File
import java.io.FileOutputStream
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify


class AudioVideoMergerTest {
    @Test fun GetVideoDataFromUri_test() {
        val convertedFile = AudioVideoMerger.getConvertedFile("myfolder", "xd")

        assertEquals(convertedFile.absolutePath, "/myfolder/xd")
    }

    @Test fun AudioVideoMerger_test() {
        val videoInputStream = InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(R.raw.video);
        val videoFile = File.createTempFile("files", "video")
        IOUtils.copy(videoInputStream, FileOutputStream(videoFile))

        val audioInputStream = InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(R.raw.audio);
        val audioFile = File.createTempFile("files", "audio")
        IOUtils.copy(audioInputStream, FileOutputStream(audioFile))

        val outputFile = File.createTempFile("files", "output")

        val cb = Mockito.mock(FFMpegCallback::class.java)

        AudioVideoMerger.with(InstrumentationRegistry.getInstrumentation().targetContext)
            .setAudioFile(audioFile)
            .setVideoFile(videoFile)
            .setAudioStartMs((0).toInt())
            .setAudioDurationMs(1000)
            .setOutputPath(outputFile!!.absolutePath)
            .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp4")
            .setCallback(cb)
            .merge()

        verify(cb,times(1)).onProgress(anyString());
    }
}
