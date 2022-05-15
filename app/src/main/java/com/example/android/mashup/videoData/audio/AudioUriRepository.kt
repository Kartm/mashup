package com.example.android.mashup.videoData.audio

import androidx.lifecycle.LiveData

class AudioUriRepository(private val videoUriDao : AudioUriDao) {

    val readAllData : LiveData<List<AudioUri>> = videoUriDao.readAllData();

    suspend fun addVideoUri(videoUri: AudioUri) = videoUriDao.addVideoUri(videoUri);

    suspend fun nukeTable() = videoUriDao.nukeTable();
}