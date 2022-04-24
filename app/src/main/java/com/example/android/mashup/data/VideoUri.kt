package com.example.android.mashup.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_uri_table")
data class VideoUri (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val uri: String
)