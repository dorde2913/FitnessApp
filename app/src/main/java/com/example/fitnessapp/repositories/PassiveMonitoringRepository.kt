package com.example.fitnessapp.repositories

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.PassiveMonitoringClient
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import com.example.fitnessapp.data.database.WorkoutDao
import com.example.fitnessapp.services.PassiveGoalsService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PassiveMonitoringRepository @Inject constructor(
    @ApplicationContext val context: Context,
    val dao: WorkoutDao
){
    val healthClient: HealthServicesClient = HealthServices.getClient(context)
    val passiveMonitoringClient: PassiveMonitoringClient = healthClient.passiveMonitoringClient

    suspend fun resetDailyBPM(hour: Int){
        dao.resetDailyBPM(hour)
    }


    val hrMaxes = dao.getHRMaxes()


    val _steps = MutableStateFlow(0)
    val steps = _steps.asStateFlow()

    val _calories = MutableStateFlow(0)
    val calories = _calories.asStateFlow()


    private val passiveListenerConfig = PassiveListenerConfig(
        dataTypes = setOf(DataType.STEPS_DAILY,DataType.HEART_RATE_BPM,DataType.CALORIES_DAILY),
        shouldUserActivityInfoBeRequested = false,
        dailyGoals = setOf(),
        healthEventTypes = setOf()
    )

    suspend fun subscribe(){
        passiveMonitoringClient.setPassiveListenerServiceAsync(
            PassiveGoalsService::class.java,
            passiveListenerConfig
        )

        println("subbed")
    }


    suspend fun getMaxHR(hour: Int) =
        dao.getMaxHR(hour)

    fun setMaxHr(hour: Int, value: Int) =
        dao.updateMaxHR(value = value, hour = hour)

//    fun setMaxDailyHR(value: Int) =
//        dao.setMaxHR(value)
//
//    fun setMinDailyHR(value: Int) =
//        dao.setMinHR(value)
//
//    fun setTimeExercising(value: Int) =
//        dao.setTimeExercising(value)
}