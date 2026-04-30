package com.example.lista8.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val city: String,
    val description: String,
    val category: String,
    val visited: Boolean = false
)