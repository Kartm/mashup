package com.example.android.mashup.videoData.video

import androidx.lifecycle.LiveData

class VideoUriRepository(private val videoUriDao : VideoUriDao) {

    val readAllData : LiveData<List<VideoUri>> = videoUriDao.readAllData();

    suspend fun addVideoUri(videoUri: VideoUri) = videoUriDao.addVideoUri(videoUri);

    suspend fun nukeTable() = videoUriDao.nukeTable();
}