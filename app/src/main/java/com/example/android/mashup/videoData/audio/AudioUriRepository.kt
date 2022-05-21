package com.example.android.mashup.videoData.audio

import androidx.lifecycle.LiveData

class AudioUriRepository(private val audioUriDao : AudioUriDao) {

    val readAllData : LiveData<List<AudioUri>> = audioUriDao.readAllData();

    suspend fun addAudioUri(videoUri: AudioUri) = audioUriDao.addAudioUri(videoUri);

    suspend fun nukeTable() = audioUriDao.nukeTable();
}