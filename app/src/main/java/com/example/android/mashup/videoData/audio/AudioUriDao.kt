package com.example.android.mashup.videoData.audio

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AudioUriDao {
    @Insert()
    fun addAudioUri(audioUri: AudioUri)

    @Query("SELECT * FROM audio_uri_table ORDER BY id ASC")
    fun readAllData() : LiveData<List<AudioUri>>

    @Query("DELETE FROM audio_uri_table")
    fun nukeTable()
}