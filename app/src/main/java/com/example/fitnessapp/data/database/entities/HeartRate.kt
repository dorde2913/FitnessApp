package com.example.fitnessapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Heartrate")
data class HeartRate(
    @PrimaryKey(autoGenerate = false) val hour: Int = 0,
    val value: Int
)