package com.example.android.mashup.videoData.audio

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [AudioUri::class], version = 1, exportSchema = false)
abstract class AudioUriDatabase : RoomDatabase() {
    abstract fun videoUriDao(): AudioUriDao;

    companion object {
        @Volatile
        private var INSTANCE: AudioUriDatabase? = null;

        fun getDatabase(context: Context): AudioUriDatabase {
            val tempInstance = INSTANCE;
            if (tempInstance != null) {
                return tempInstance;
            }
            synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AudioUriDatabase::class.java,
                    "video_uri_database"
                ).build();
                INSTANCE = instance;
                return instance;
            }
        }
    }
}