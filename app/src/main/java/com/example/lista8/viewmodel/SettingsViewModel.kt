package com.example.lista8.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    var isMonochromatic by mutableStateOf(false)
        private set

    fun toggleMonochromatic(enabled: Boolean) {
        isMonochromatic = enabled
    }
}