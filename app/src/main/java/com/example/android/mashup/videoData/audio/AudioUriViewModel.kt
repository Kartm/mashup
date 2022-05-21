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
        val audioUriDao = AudioUriDatabase.getDatabase(application).audioUriDao()
        repository = AudioUriRepository(audioUriDao);
        readAllData = repository.readAllData;
    }

    fun addAudioUri(audioUri: AudioUri)
    {
        viewModelScope.launch(Dispatchers.IO){
            repository.addAudioUri(audioUri);
        }
    }

    fun nukeTable()
    {
        viewModelScope.launch(Dispatchers.IO){
            repository.nukeTable();
        }
    }
}