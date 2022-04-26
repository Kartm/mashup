package com.example.android.mashup.utils

import android.content.Context
import android.util.Log
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

        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        val query = arrayOf("-i", audio!!.path, "-filter_complex", "showwavespic=s=640x120", "-frames:v", "1", outputLocation.path)

        // todo generate waveform from ffmpeg
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun statisticsProcess(statistics: Statistics) {
//                Log.i("FFMPEG LOG : ", statistics.videoFrameNumber.toString())
            }

            override fun process(logMessage: LogMessage) {
//                Log.i("FFMPEG LOG : ", logMessage.text)
//                callback!!.onProgress(logMessage.text)
            }

            override fun success() {
                Log.v("me", "waveform ready: $outputLocation")
//                Utils.refreshGallery(outputLocation.path, context)
//                callback!!.onSuccess(outputLocation, OutputType.TYPE_VIDEO)
            }

            override fun cancel() {
//                callback!!.onFailure(IOException("Canceled"))
            }

            override fun failed() {
//                if (outputLocation.exists()) {
//                    outputLocation.delete()
//                }
//                callback!!.onFailure(IOException("Failed"))
            }
        })
    }

    companion object {

        val TAG = "AudioWaveformGenerator"

        fun with(context: Context): AudioWaveformGenerator {
            return AudioWaveformGenerator(context)
        }
    }
}