package com.example.android.mashup

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.android.mashup.utils.AudioVideoMerger
import org.junit.Test
import org.junit.Assert.*

class AudioVideoMergerTest {
    @Test fun GetVideoDataFromUri_test() {
        val convertedFile = AudioVideoMerger.getConvertedFile("myfolder", "xd")

        assertEquals(convertedFile.absolutePath, "/myfolder/xd")
    }
}
