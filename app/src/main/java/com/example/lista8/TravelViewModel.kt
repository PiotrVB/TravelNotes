package com.example.lista8

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed interface TravelUiState {
    data class Success(val fact: String) : TravelUiState
    object Error : TravelUiState
    object Loading : TravelUiState
}

class TravelViewModel : ViewModel() {
    var uiState: TravelUiState by mutableStateOf(TravelUiState.Loading)
        private set

    fun getNewFact() {
        viewModelScope.launch {
            uiState = TravelUiState.Loading
            uiState = try {
                val response = RetrofitClient.apiService.getRandomFact()
                TravelUiState.Success(response.text)
            } catch (e: Exception) {
                TravelUiState.Error
            }
        }
    }

    init {
        getNewFact()
    }
}