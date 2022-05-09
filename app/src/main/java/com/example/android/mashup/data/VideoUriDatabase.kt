package com.example.android.mashup.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [VideoUri::class], version = 1, exportSchema = false)
abstract class VideoUriDatabase : RoomDatabase() {
    abstract fun videoUriDao(): VideoUriDao;

    companion object {
        @Volatile
        private var INSTANCE: VideoUriDatabase? = null;

        fun getDatabase(context: Context): VideoUriDatabase {
            val tempInstance = INSTANCE;
            if (tempInstance != null) {
                return tempInstance;
            }
            synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideoUriDatabase::class.java,
                    "video_uri_database"
                ).build();
                INSTANCE = instance;
                return instance;
            }
        }
    }
}