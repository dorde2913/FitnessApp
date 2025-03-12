package com.example.fitnessapp.database.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.fitnessapp.presentation.stateholders.WorkoutType


@Entity(tableName = "Workout")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String = "",
    val color: Int = Color.Yellow.toArgb(),
    val workoutType: WorkoutType = WorkoutType.GYM
)

class ColorTypeConverters{
    @TypeConverter
    fun fromColor(value: Color): Int {
        return value.toArgb()
    }

    @TypeConverter
    fun intToColor(value: Int): Color{
        return Color(value)
    }
}