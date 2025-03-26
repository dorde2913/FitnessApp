package com.example.fitnessapp.data.handheld

import kotlinx.serialization.Serializable

@Serializable
data class BPMUpdate(
    val id: Long,
    val label: String,
    val length: Long,
    val BPMList: List<Int>
)

@Serializable
data class LocationUpdate(
    val id: Long,
    val label: String,
    val locationList: List<Pair<Double,Double>>
)

@Serializable
data class CaloriesUpdate(
    val id: Long,
    val label: String,
    val calories: Int

)

@Serializable
data class DistanceUpdate(
    val id: Long,
    val label: String,
    val distance: Int
)
@Serializable
data class DailyCaloriesUpdate(
    val calories: Int = 0
)
@Serializable
data class DailyStepsUpdate(
    val steps:Int = 0
)
@Serializable
data class DailyHeartRateUpdate(
    val hrList: List<Int> = listOf()
)