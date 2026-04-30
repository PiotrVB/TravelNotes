package com.example.lista8.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Query("SELECT * FROM places ORDER BY id DESC")
    fun getAllPlaces(): Flow<List<Place>>

    @Insert
    suspend fun insertPlace(place: Place)

    @Update
    suspend fun updatePlace(place: Place)

    @Delete
    suspend fun deletePlace(place: Place)
}