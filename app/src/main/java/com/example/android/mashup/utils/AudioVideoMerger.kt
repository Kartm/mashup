package com.example.android.mashup.utils

import android.content.Context
import android.util.Log
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage
import com.simform.videooperations.Statistics
import java.io.File
import java.io.IOException

interface FFMpegCallback {
    fun onProgress(progress: String) {}
    fun onSuccess(convertedFile: File, type: OutputType)
    fun onFailure(error: Exception) {}
    fun onNotAvailable(error: Exception) {}
    fun onFinish() {}
}

object Utils {
    fun getConvertedFile(folder: String, fileName: String): File {
        val f = File(folder)

        if (!f.exists())
            f.mkdirs()

        return File(f.path + File.separator + fileName)
    }
}

class AudioVideoMerger private constructor(private val context: Context) {
    private var audio: File? = null
    private var video: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""
    private var audioStartMs: Int? = null
    private var audioDurationMs: Int? = null

    fun setAudioFile(originalFiles: File): AudioVideoMerger {
        this.audio = originalFiles
        return this
    }

    fun setVideoFile(originalFiles: File): AudioVideoMerger {
        this.video = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): AudioVideoMerger {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): AudioVideoMerger {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): AudioVideoMerger {
        this.outputFileName = output
        return this
    }

    fun setAudioStartMs(ms: Int): AudioVideoMerger {
        this.audioStartMs = ms
        return this
    }

    fun setAudioDurationMs(ms: Int): AudioVideoMerger {
        this.audioDurationMs = ms
        return this
    }

    fun merge() {

        if (video == null || !video!!.exists()) {
            callback!!.onFailure(IOException("Video file not exists"))
            return
        }
        if (audio == null || !audio!!.exists()) {
            callback!!.onFailure(IOException("Audio file not exists"))
            return
        }
        if (!audio!!.canRead() || !video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        // todo replace audio stream https://superuser.com/a/277667
        // todo -shortest https://superuser.com/questions/277642/how-to-merge-audio-and-video-file-in-ffmpeg#comment1484018_862903

        // todo -ss 10 -i audio.mp3 starts audio at 10s
        // todo -t 6 -i audio.mp3 takes just 6s of audio
        // you can use time format ffmpeg -i input.mp3 -ss 00:02:54.583 -acodec copy output.mp3

        val query = arrayOf(
            "-i",
            video!!.path,
            "-ss",
            audioStartMs.toString() + "ms",
            "-t",
            audioDurationMs.toString() + "ms",
            "-i",
            audio!!.path,
            "-c:v",
            "copy",
            "-c:a",
            "copy",
            "-strict",
            "experimental",
            "-map",
            "0:v:0",
            "-map",
            "1:a:0",
            "-shortest",
            outputLocation.path
        )

        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun statisticsProcess(statistics: Statistics) {
                Log.i("FFMPEG LOG : ", statistics.videoFrameNumber.toString())
            }

            override fun process(logMessage: LogMessage) {
                Log.i("FFMPEG LOG : ", logMessage.text)
                callback!!.onProgress(logMessage.text)
            }

            override fun success() {
                callback!!.onSuccess(outputLocation, OutputType.VIDEO)
            }

            override fun cancel() {
                callback!!.onFailure(IOException("Canceled"))
            }

            override fun failed() {
                if (outputLocation.exists()) {
                    outputLocation.delete()
                }
                callback!!.onFailure(IOException("Failed"))
            }
        })
    }

    companion object {
        fun with(context: Context): AudioVideoMerger {
            return AudioVideoMerger(context)
        }
    }
}

