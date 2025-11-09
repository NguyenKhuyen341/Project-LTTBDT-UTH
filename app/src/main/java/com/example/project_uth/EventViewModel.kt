package com.example.project_uth

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val eventDao: EventDao

    val allEvents: Flow<List<Event>>

    init {
        val database = EventDatabase.getDatabase(application)
        eventDao = database.eventDao()
        allEvents = eventDao.getAllEvents()
    }

    fun insertEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        eventDao.insertEvent(event)
    }

    fun updateEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        eventDao.updateEvent(event)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        eventDao.deleteEvent(event)
    }
}


class EventViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}