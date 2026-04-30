package com.example.lista8

// Dodaj (private val apiService: CatFactService) tutaj:
class CatFactRepository(private val apiService: CatFactService) {
    suspend fun getFact() = apiService.getCatFact()
}