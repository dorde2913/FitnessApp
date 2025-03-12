package com.example.fitnessapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.database.entities.Workout
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM Workout WHERE workoutType = :type")
    fun getWorkoutsByType(type: WorkoutType): Flow<List<Workout>>

    @Query("SELECT * FROM Workout WHERE label = :label")
    fun getWorkoutByLabel(label: String): Flow<Workout>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(workout: Workout)
}