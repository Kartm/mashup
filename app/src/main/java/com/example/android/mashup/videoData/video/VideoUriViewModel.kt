package com.example.android.mashup.videoData.video

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoUriViewModel(application: Application) : AndroidViewModel(application) {

    var readAllData: LiveData<List<VideoUri>>;
    private val repository: VideoUriRepository;

    init{
        val videoUriDao = VideoUriDatabase.getDatabase(application).videoUriDao()
        repository = VideoUriRepository(videoUriDao);
        readAllData = repository.readAllData;
    }

    fun addVideoUri(videoUri: VideoUri)
    {
        viewModelScope.launch(Dispatchers.IO){
            repository.addVideoUri(videoUri);
        }
    }

    fun nukeTable()
    {
        viewModelScope.launch(Dispatchers.IO){
            repository.nukeTable();
        }
    }
}