package com.example.fitnessapp.data.database

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fitnessapp.data.database.entities.HeartRate

import com.example.fitnessapp.data.database.entities.Workout
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Workout::class,HeartRate::class), version = 1, exportSchema = false)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workouts"
                ).addCallback(WordDatabaseCallback(scope)).build()
                INSTANCE = instance
                // return instance
                return@synchronized instance
            }
        }
    }

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch { populateDatabase(database.workoutDao()) }
            }
        }

        suspend fun populateDatabase(workoutDao: WorkoutDao) {
            workoutDao.insert(Workout(label = "Default Gym Workout", color = Color.Black.toArgb()
                , workoutType = WorkoutType.GYM))
            workoutDao.insert(Workout(label = "Push", color = Color.Red.toArgb()
                , workoutType = WorkoutType.GYM))
            workoutDao.insert(Workout(label = "Pull", color = Color.Blue.toArgb()
                , workoutType = WorkoutType.GYM))
            workoutDao.insert(Workout(label = "Legs", color = Color.Green.toArgb()
                , workoutType = WorkoutType.GYM))

            workoutDao.insert(Workout(label = "Default Cardio Workout", color = Color.Black.toArgb()
                , workoutType = WorkoutType.CARDIO))
            workoutDao.insert(Workout(label = "Running", color = Color.Red.toArgb()
                , workoutType = WorkoutType.CARDIO))
            workoutDao.insert(Workout(label = "Cycling", color = Color.Blue.toArgb()
                , workoutType = WorkoutType.CARDIO))


            for (i in 0 until 24){
                workoutDao.insert(HeartRate(hour = i , value = 0))
            }

            //workoutDao.insert(PassiveStats())

        }
    }
}