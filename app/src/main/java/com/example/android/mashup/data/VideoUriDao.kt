package com.example.android.mashup.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VideoUriDao {
    @Insert()
    fun addVideoUri(videoUri: VideoUri)

    @Query("SELECT * FROM video_uri_table ORDER BY id ASC")
    fun readAllData() : LiveData<List<VideoUri>>

    @Query("DELETE FROM video_uri_table")
    fun nukeTable()
}