package com.example.android.mashup.utils

import android.content.Context
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException

import java.io.File
import java.io.IOException
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.FileNotFoundException
import java.io.InputStream

interface FFMpegCallback {

    fun onProgress(progress: String)

    fun onSuccess(convertedFile: File, type: String)

    fun onFailure(error: Exception)

    fun onNotAvailable(error: Exception)

    fun onFinish()

}

object Constants {
    val APP_FOLDER = "FFMpeg Example"
}

object Utils {

    val outputPath: String
        get() {
            val path = Environment.getExternalStorageDirectory().toString() + File.separator + Constants.APP_FOLDER + File.separator

            val folder = File(path)
            if (!folder.exists())
                folder.mkdirs()

            return path
        }

    fun copyFileToExternalStorage(resourceId: Int, resourceName: String, context: Context): File {
        val pathSDCard = outputPath + resourceName
        try {
            val inputStream = context.resources.openRawResource(resourceId)
            inputStream.toFile(pathSDCard)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return File(pathSDCard)
    }

    fun InputStream.toFile(path: String) {
        File(path).outputStream().use { this.copyTo(it) }
    }

    fun getConvertedFile(folder: String, fileName: String): File {
        val f = File(folder)

        if (!f.exists())
            f.mkdirs()

        return File(f.path + File.separator + fileName)
    }

    fun refreshGallery(path: String, context: Context) {

        val file = File(path)
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours.toString() + ":"
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds
        } else {
            secondsString = "" + seconds
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString

        // return timer string
        return finalTimerString
    }
}


/**
 * Created by Umair_Adil on 19/09/2016.
 */

class OutputType {

    companion object {

        var TYPE_VIDEO = "video"
        var TYPE_MULTIPLE_VIDEO = "multiple_video"
        var TYPE_AUDIO = "audio"
        var TYPE_GIF = "gif"
        var TYPE_IMAGES = "images"
    }
}

class AudioVideoMerger private constructor(private val context: Context) {

    private var audio: File? = null
    private var video: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""

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

        val cmd = arrayOf("-i", video!!.path, "-i", audio!!.path, "-c:v", "copy", "-c:a", "aac", "-strict", "experimental", "-map", "0:v:0", "-map", "1:a:0", "-shortest", outputLocation.path)

        try {
            FFmpeg.getInstance(context).execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {}

                override fun onProgress(message: String?) {
                    callback!!.onProgress(message!!)
                }

                override fun onSuccess(message: String?) {
                    Utils.refreshGallery(outputLocation.path, context)
                    callback!!.onSuccess(outputLocation, OutputType.TYPE_VIDEO)

                }

                override fun onFailure(message: String?) {
                    if (outputLocation.exists()) {
                        outputLocation.delete()
                    }
                    callback!!.onFailure(IOException(message))
                }

                override fun onFinish() {
                    callback!!.onFinish()
                }
            })
        } catch (e: Exception) {
            callback!!.onFailure(e)
        } catch (e2: FFmpegCommandAlreadyRunningException) {
            callback!!.onNotAvailable(e2)
        }

    }

    companion object {

        val TAG = "AudioVideoMerger"

        fun with(context: Context): AudioVideoMerger {
            return AudioVideoMerger(context)
        }
    }
}