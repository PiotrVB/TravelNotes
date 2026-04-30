package com.example.lista8

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "TravelNotesApp/1.0 (kontakt@travelnotes.pl)")
                .build()
            chain.proceed(request)
        }
        .build()

    val apiService: TravelService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pl.wikipedia.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TravelService::class.java)
    }
}