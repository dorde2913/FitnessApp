package com.example.fitnessapp.services

import android.content.Context
import android.content.Intent
import android.preference.PreferenceDataStore
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType

import com.example.fitnessapp.presentation.MAX_KEY
import com.example.fitnessapp.presentation.MIN_KEY
import com.example.fitnessapp.presentation.dataStore
import com.example.fitnessapp.repositories.ExerciseClientRepository
import com.example.fitnessapp.repositories.PassiveMonitoringRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class PassiveGoalsService : PassiveListenerService(){

    @Inject lateinit var repository: PassiveMonitoringRepository
    @Inject lateinit var exerciseClientRepository: ExerciseClientRepository

    private var flag = false
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        CoroutineScope(Dispatchers.Default).launch {
            while(true){
                println("passive service running")
                delay(5000)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        super.onNewDataPointsReceived(dataPoints)
        println("BUHBUH, ${dataPoints.sampleDataPoints}")
        if(dataPoints.getData(DataType.STEPS_DAILY).isNotEmpty()){
            Log.d("PASSIVE MONITORING SERVICE",
                "STEPS: ${dataPoints.getData(DataType.STEPS_DAILY)[0].value}")
            repository._steps.value = dataPoints.getData(DataType.STEPS_DAILY)[0].value.toInt()
            println(repository._steps.value)

            exerciseClientRepository.sendSteps(repository._steps.value)
        }

        if (dataPoints.getData(DataType.CALORIES_DAILY).isNotEmpty()){
            repository._calories.value =
                dataPoints.getData(DataType.CALORIES_DAILY)[0].value.toInt()

            exerciseClientRepository.sendCaloriesDaily(repository._calories.value)
        }

        if (dataPoints.getData(DataType.HEART_RATE_BPM).isNotEmpty()){
            val heartrate = dataPoints.getData(DataType.HEART_RATE_BPM)[0].value.toInt()
            Log.d("PASSIVE MONITORING SERVICE",
                "HEARTRATE: $heartrate")

            CoroutineScope(Dispatchers.IO).launch {
                val hour = Instant.ofEpochSecond(Instant.now().epochSecond).atZone(
                    ZoneId.systemDefault()).hour


                println("HOUR: $hour")



                val max = repository.getMaxHR(hour)

                this@PassiveGoalsService.dataStore.edit { preferences->
                    if (preferences[MIN_KEY] == null){
                        preferences[MIN_KEY] = heartrate
                    }
                    if (preferences[MAX_KEY] == null){
                        preferences[MAX_KEY] = heartrate
                    }
                }

                if (max!=null)
                    if (heartrate > max.value){
                        repository.setMaxHr(hour = max.hour, value = heartrate)
                    }
            }

        }
    }

}