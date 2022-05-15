package com.example.android.mashup.videoData.audio

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_uri_table")
data class AudioUri (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val uri: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val thumbnail : ByteArray,
    val name: String,
    val length : Long,
)