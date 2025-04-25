package com.example.fitnessapp.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.PassiveMonitoringClient
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import com.example.fitnessapp.data.database.WorkoutDao
import com.example.fitnessapp.presentation.MAX_KEY
import com.example.fitnessapp.presentation.MIN_KEY
import com.example.fitnessapp.presentation.dataStore
import com.example.fitnessapp.services.PassiveGoalsService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
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

    suspend fun getHRMaxesNoFlow() =
        dao.getHRMaxesNoFlow()




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


    suspend fun setMaxHr(hour: Int, value: Int) =
        dao.updateMaxHR(value = value, hour = hour)



    fun sendMinMax(max: Int, min: Int) = CoroutineScope(Dispatchers.IO).launch {
        val hour = Instant.ofEpochSecond(Instant.now().epochSecond).atZone(
            ZoneId.systemDefault()).hour

        val _max = dao.getMaxHR(hour)

        if (max > _max!!.value) dao.updateMaxHR(max, hour)

        context.dataStore.edit { preferences->
            if (preferences[MIN_KEY] == null){
                preferences[MIN_KEY] = min
            }
            else if (preferences[MIN_KEY]!! > min) preferences[MIN_KEY] = min
            if (preferences[MAX_KEY] == null){
                preferences[MAX_KEY] = max
            }
            else if (preferences[MAX_KEY]!! < max) preferences[MAX_KEY] = max
        }
    }






//    fun setMaxDailyHR(value: Int) =
//        dao.setMaxHR(value)
//
//    fun setMinDailyHR(value: Int) =
//        dao.setMinHR(value)
//
//    fun setTimeExercising(value: Int) =
//        dao.setTimeExercising(value)
}