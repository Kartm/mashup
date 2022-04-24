package com.example.android.mashup

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

var videoList = mutableListOf<Video>()

val VIDEO_ID_EXTRA = "videoExtra"

@Parcelize
class Video(
    var thubnail: Bitmap,
    var title: String,
    var duration: Number,
    var description: String,
    val id: Int? = videoList.size
) : Parcelable