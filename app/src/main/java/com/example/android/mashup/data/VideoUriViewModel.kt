package com.example.android.mashup.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoUriViewModel(application: Application) : AndroidViewModel(application) {

    private val readAllData: LiveData<List<VideoUri>>;
    private val repository: VideoUriRepository;

    init{
        val videoUriDao = VideoUriDatabase.getDatabase(application).videoUriDao()
        repository = VideoUriRepository(videoUriDao);
        readAllData = repository.readAllData;
    }

    fun addVideoUri(videoUri: VideoUri)
    {
        viewModelScope.launch(Dispatchers.IO){
            repository.addVideUri(videoUri);
        }
    }
}