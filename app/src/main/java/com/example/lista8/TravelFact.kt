package com.example.lista8

import com.google.gson.annotations.SerializedName

data class TravelFact(
    @SerializedName("extract")
    val text: String,
    val title: String
)