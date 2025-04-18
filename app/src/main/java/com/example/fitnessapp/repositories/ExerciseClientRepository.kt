package com.example.fitnessapp.repositories

import android.content.Context
import android.util.Log
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.BatchingMode
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseEndReason
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseTrackedStatus
import androidx.health.services.client.data.ExerciseTrackedStatus.Companion.NO_EXERCISE_IN_PROGRESS
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import com.example.fitnessapp.data.handheld.HandheldClient
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseClientRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val handheldClient: HandheldClient
) {
    private val healthServicesClient = HealthServices.getClient(context)
    private val exerciseClient = healthServicesClient.exerciseClient



    private var dataTypes = setOf(
        DataType.HEART_RATE_BPM,
        DataType.CALORIES_TOTAL,
        DataType.SPEED,
        DataType.LOCATION,
        DataType.DISTANCE_TOTAL
    )

    var length = 0L

    var currentType = WorkoutType.GYM
    var currentLabel = ""

    val _sensorState = MutableStateFlow(false)
    val sensorState = _sensorState.asStateFlow()

    val _heartRate = MutableStateFlow(0.0)
    val currentHeartRate = _heartRate.asStateFlow()

    val _calories = MutableStateFlow(0.0)
    var totalCals = _calories.asStateFlow()

    val _distance = MutableStateFlow(0.0)
    val totalDistance = _distance.asStateFlow()

    val _speed = MutableStateFlow(0.0)
    val currentSpeed = _speed.asStateFlow()

    val ongoing = MutableStateFlow(false)
    val isOngoing = ongoing.asStateFlow()


    val _avgBPM = MutableStateFlow(0.0)
    val averageBPM = _avgBPM.asStateFlow()
    var bpm_counter = 0



    private var config = ExerciseConfig.builder(exerciseType = ExerciseType.RUNNING)
        .setDataTypes(dataTypes)
        .setIsGpsEnabled(true)
        .setIsAutoPauseAndResumeEnabled(false)
        .setBatchingModeOverrides(setOf(BatchingMode.HEART_RATE_5_SECONDS))
        .build()


    var BPMList: MutableList<Int> = mutableListOf()
    var locationList: MutableList<Pair<Double,Double>> = mutableListOf()

    var timestamp = 0L

    //za kalorije i distancu ne mora lista uopste


    val callback = object: ExerciseUpdateCallback{
        override fun onAvailabilityChanged(dataType: DataType<*, *>, availability: Availability) {
            Log.d("ExerciseUpdateCallback", "Availability Changed")
        }


        override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
            /*
            update
             */
            val exerciseStateInfo = update.exerciseStateInfo
            val latestMetrics = update.latestMetrics

            if (exerciseStateInfo.state == ExerciseState.USER_PAUSED) return

            if (exerciseStateInfo.state == ExerciseState.ACTIVE) ongoing.value = true
            else ongoing.value = false


           // printEnded(exerciseStateInfo.state.isEnded,exerciseStateInfo.endReason)

            /* BPM */
            if (latestMetrics.getData(DataType.HEART_RATE_BPM).isNotEmpty()){
                _heartRate.value = latestMetrics.getData(DataType.HEART_RATE_BPM).last().value
                Log.d("Heart rate value",_heartRate.value.toString())

                for (element in  latestMetrics.getData(DataType.HEART_RATE_BPM)){
                    if (ongoing.value == false) break
                    BPMList.add(element.value.toInt())
                    bpm_counter++
                    _avgBPM.value = (_avgBPM.value * (bpm_counter-1) +
                        element.value.toInt() ) / bpm_counter
                }

                if (exerciseStateInfo.state == ExerciseState.PREPARING){
                    println("PREPARING")
                    if (_heartRate.value == 0.0)_sensorState.value = false
                    else _sensorState.value = true
                }
            }
            /* CALORIES */
            if (latestMetrics.getData(DataType.CALORIES_TOTAL)?.total != null){
                _calories.value = latestMetrics.getData(DataType.CALORIES_TOTAL)!!.total
            }
            //speed,location,steps

            if (latestMetrics.getData(DataType.LOCATION).isNotEmpty()){
                Log.d("LOCATION", latestMetrics.getData(DataType.LOCATION)[0].value.toString())
                for (element in latestMetrics.getData(DataType.LOCATION)){
                    locationList.add(Pair(element.value.latitude,element.value.longitude))
                }
                //ovo za rutu
            }

            if (latestMetrics.getData(DataType.SPEED).isNotEmpty()){
                Log.d("SPEED", latestMetrics.getData(DataType.SPEED)[0].value.toString())
                _speed.value =latestMetrics.getData(DataType.SPEED)[0].value
            }

            if (latestMetrics.getData(DataType.DISTANCE_TOTAL)!=null){
                Log.d("DISTANCE", latestMetrics.getData(DataType.DISTANCE_TOTAL)!!.total.toString())
                _distance.value = latestMetrics.getData(DataType.DISTANCE_TOTAL)!!.total
            }

        }

        override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
            Log.d("ExerciseUpdateCallback", "Lap Summary Received")
        }

        override fun onRegistered() {
            Log.d("ExerciseUpdateCallback", "Registered")
        }

        override fun onRegistrationFailed(throwable: Throwable) {
            Log.d("ExerciseUpdateCallback", "Registration Failed")
        }

    }

    fun pauseExercise(){
        CoroutineScope(Dispatchers.Default).launch {
            exerciseClient.pauseExercise()
        }
    }
    fun unpauseExercise(){
        CoroutineScope(Dispatchers.Default).launch {
            exerciseClient.resumeExercise()
        }
    }

    fun prepareExercise(){

        CoroutineScope(Dispatchers.Default).launch {
            exerciseClient.setUpdateCallback(callback)
            if (exerciseClient.getCurrentExerciseInfo().exerciseTrackedStatus != NO_EXERCISE_IN_PROGRESS){
                exerciseClient.endExercise()
            }
            exerciseClient.prepareExercise(
                WarmUpConfig(ExerciseType.RUNNING, dataTypes = setOf(
                    DataType.HEART_RATE_BPM,
                    DataType.CALORIES,
                    DataType.SPEED,
                    DataType.LOCATION,
                    DataType.DISTANCE
                ))
            )
        }
    }

    fun startExercise(){
        length = 0L
        bpm_counter = 0
        timestamp = Instant.now().epochSecond
        Log.d("EXERCISE START","TYPE: $currentType")
        if (currentType == WorkoutType.GYM){
            dataTypes = setOf(
                DataType.HEART_RATE_BPM,
                DataType.CALORIES_TOTAL
            )
        }
        else {
            dataTypes = setOf(
                DataType.HEART_RATE_BPM,
                DataType.CALORIES_TOTAL,
                DataType.LOCATION,
                DataType.DISTANCE_TOTAL,
                DataType.SPEED,
            )
        }

        config = ExerciseConfig.builder(exerciseType = ExerciseType.RUNNING)
            .setDataTypes(dataTypes)
            .setIsGpsEnabled(true)
            .setIsAutoPauseAndResumeEnabled(false)
            .setBatchingModeOverrides(setOf(BatchingMode.HEART_RATE_5_SECONDS))
            .build()

        CoroutineScope(Dispatchers.Default).launch {
           exerciseClient.setUpdateCallback(callback)
           exerciseClient.startExercise(config)
        }
    }

    fun endExercise(){
        CoroutineScope(Dispatchers.Default).launch {
            //exerciseClient.setUpdateCallback(callback)
            exerciseClient.endExercise()
        }
    }



    fun sendHRToHandheld() {
        handheldClient.sendHRToHandheld(
            timestamp = timestamp,
            BPMList = BPMList,
            currentLabel = currentLabel,
            length = length)
        BPMList = mutableListOf()
    }

    fun sendCalories() =
        handheldClient.sendCalories(
            timestamp = timestamp,
            calories = _calories.value.toInt(),
            currentLabel = currentLabel)

    fun sendDistance() =
        handheldClient.sendDistance(
            timestamp = timestamp,
            distance = _distance.value.toInt(),
            currentLabel = currentLabel
        )

    fun sendLocationToHandheld(){
        handheldClient.sendLocationToHandheld(
            timestamp = timestamp,
            locationList = locationList,
            currentLabel = currentLabel
        )
        locationList = mutableListOf()
    }

    fun sendSpeed(){
        //
    }


}


