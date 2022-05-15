package com.example.android.mashup.videoData.audio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioUriViewModel(application: Application) : AndroidViewModel(application) {

    var readAllData: LiveData<List<AudioUri>>;
    private val repository: AudioUriRepository;

    init{
        val videoUriDao = AudioUriDatabase.getDatabase(application).videoUriDao()
        repository = AudioUriRepository(videoUriDao);
        readAllData = repository.readAllData;
    }

    fun addVideoUri(videoUri: AudioUri)
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