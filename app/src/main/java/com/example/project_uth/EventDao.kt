package com.example.project_uth

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<Event>>

    @Insert
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)


    @Delete
    suspend fun deleteEvent(event: Event)
}