package com.example.fitnessapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.data.database.entities.HeartRate

import com.example.fitnessapp.data.database.entities.Workout
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


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(heartRate: HeartRate)

    @Query("SELECT * FROM Heartrate WHERE hour = :hour")
    suspend fun getMaxHR(hour: Int): HeartRate?

    @Query("UPDATE Heartrate SET value = :value WHERE hour = :hour")
    suspend fun updateMaxHR(value: Int, hour: Int)

    @Query("SELECT * FROM Heartrate ORDER BY hour")
    fun getHRMaxes(): Flow<List<HeartRate>>

    @Query("UPDATE Heartrate SET value = 0 WHERE hour > :hour")
    suspend fun resetDailyBPM(hour: Int)

    @Query("SELECT * FROM Heartrate ORDER BY hour")
    suspend fun getHRMaxesNoFlow(): List<HeartRate>

    @Query("DELETE FROM Workout")
    suspend fun clearLabels()


//    @Query("UPDATE PassiveStats SET minHR = :value")
//    fun setMinHR(value: Int)
//
//    @Query("UPDATE PassiveStats SET maxHR = :value")
//    fun setMaxHR(value: Int)
//
//    @Query("UPDATE PassiveStats SET timeExercising = :value")
//    fun setTimeExercising(value: Int)
}