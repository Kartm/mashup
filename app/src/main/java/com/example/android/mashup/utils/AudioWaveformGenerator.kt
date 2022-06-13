package com.example.android.mashup.utils

import android.content.Context
import android.util.Log
import com.example.android.mashup.utils.AudioVideoMerger.Companion.getConvertedFile
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage
import com.simform.videooperations.Statistics
import java.io.File
import java.io.IOException

class AudioWaveformGenerator private constructor(private val context: Context) {
    private var audio: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""

    fun setAudioFile(originalFiles: File): AudioWaveformGenerator {
        this.audio = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): AudioWaveformGenerator {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): AudioWaveformGenerator {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): AudioWaveformGenerator {
        this.outputFileName = output
        return this
    }

    fun merge() {
        if (audio == null || !audio!!.exists()) {
            callback!!.onFailure(IOException("Audio file not exists"))
            return
        }
        if (!audio!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = getConvertedFile(outputPath, outputFileName)

        val query = arrayOf("-i", audio!!.path, "-filter_complex", "showwavespic=s=640x120:colors=gray", "-frames:v", "1", outputLocation.path)

        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun success() {
                Log.v("me", "waveform ready: $outputLocation")
                callback!!.onSuccess(outputLocation, OutputType.WAVEFORM)
            }
        })
    }
}