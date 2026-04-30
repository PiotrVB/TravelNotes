package com.example.lista8.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lista8.data.Place
import com.example.lista8.data.PlaceDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaceViewModel(application: Application) : AndroidViewModel(application) {

    private val placeDao = PlaceDatabase.getDatabase(application).placeDao()

    val places = placeDao.getAllPlaces()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addPlace(
        name: String,
        city: String,
        description: String,
        category: String
    ) {
        viewModelScope.launch {
            placeDao.insertPlace(
                Place(
                    name = name,
                    city = city,
                    description = description,
                    category = category,
                    visited = false
                )
            )
        }
    }

    fun updatePlace(place: Place) {
        viewModelScope.launch {
            placeDao.updatePlace(place)
        }
    }

    fun deletePlace(place: Place) {
        viewModelScope.launch {
            placeDao.deletePlace(place)
        }
    }

    fun toggleVisited(place: Place) {
        viewModelScope.launch {
            placeDao.updatePlace(
                place.copy(visited = !place.visited)
            )
        }
    }
}