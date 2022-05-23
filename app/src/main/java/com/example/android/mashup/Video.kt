package com.example.android.mashup

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

var videoList = mutableListOf<Video>()

val VIDEO_ID_EXTRA = "videoExtra"

@Parcelize
class Video(
    var uri : String,
    var thumbnail: Bitmap,
    var title: String,
    var duration: Number,
    val id: Int? = videoList.size
) : Parcelable