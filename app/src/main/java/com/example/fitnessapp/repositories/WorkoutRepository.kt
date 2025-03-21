package com.example.fitnessapp.repositories

import com.example.fitnessapp.data.database.WorkoutDao
import com.example.fitnessapp.data.database.entities.Workout
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import javax.inject.Inject
import javax.inject.Singleton


/* Repository for Workout database access */
@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) {

    val cardioWorkouts = workoutDao.getWorkoutsByType(WorkoutType.CARDIO)
    val gymWorkouts = workoutDao.getWorkoutsByType(WorkoutType.GYM)

    suspend fun insert(workout: Workout) =
        workoutDao.insert(workout)

    fun getWorkoutByLabel(label: String) =
        workoutDao.getWorkoutByLabel(label)
}
