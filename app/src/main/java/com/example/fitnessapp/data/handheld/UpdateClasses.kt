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