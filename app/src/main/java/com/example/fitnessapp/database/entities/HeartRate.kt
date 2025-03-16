package com.example.fitnessapp.database.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitnessapp.presentation.stateholders.WorkoutType

@Entity(tableName = "Heartrate")
data class HeartRate(
    @PrimaryKey(autoGenerate = false) val hour: Int = 0,
    val value: Int
)