package com.example.lista8

import retrofit2.http.GET

interface TravelService {
    @GET("api/rest_v1/page/random/summary")
    suspend fun getRandomFact(): TravelFact
}