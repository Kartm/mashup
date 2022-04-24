package com.example.android.mashup.data

import androidx.lifecycle.LiveData

class VideoUriRepository(private val videoUriDao : VideoUriDao) {

    val readAllData : LiveData<List<VideoUri>> = videoUriDao.readAllData();

    suspend fun addVideUri(videoUri: VideoUri) = videoUriDao.addVideoUri(videoUri);
}