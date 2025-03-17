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
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.LocationData
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer
import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseClientRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val healthServicesClient = HealthServices.getClient(context)
    private val exerciseClient = healthServicesClient.exerciseClient

    private val dataClient by lazy {Wearable.getDataClient(context)}

    private var dataTypes = setOf(
        DataType.HEART_RATE_BPM,
        DataType.CALORIES_TOTAL,
        DataType.SPEED,
        DataType.LOCATION,
        DataType.DISTANCE_TOTAL
    )

    var currentType = WorkoutType.GYM

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

    init{
        dataClient.addListener{dataEvents ->
            println("DATA CHANGED")
            dataEvents.forEach { event ->
                if (event.type == DataEvent.TYPE_DELETED) {

                    val dataItem = event.dataItem
                    if (dataItem.uri.path?.endsWith("/heartrate") == true) {
                        //sendToHandheld()
                    }
                }
            }

        }
    }


    private var config = ExerciseConfig.builder(exerciseType = ExerciseType.RUNNING)
        .setDataTypes(dataTypes)
        .setIsGpsEnabled(true)
        .setIsAutoPauseAndResumeEnabled(false)
        .setBatchingModeOverrides(setOf(BatchingMode.HEART_RATE_5_SECONDS))
        .build()


    var BPMList: MutableList<Int> = mutableListOf()
    var locationList: MutableList<LocationData> = mutableListOf()


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


            printEnded(exerciseStateInfo.state.isEnded,exerciseStateInfo.endReason)

            /* BPM */
            if (latestMetrics.getData(DataType.HEART_RATE_BPM).isNotEmpty()){
                _heartRate.value = latestMetrics.getData(DataType.HEART_RATE_BPM).last().value
                Log.d("Heart rate value",_heartRate.value.toString())

                for (element in  latestMetrics.getData(DataType.HEART_RATE_BPM)){
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
                    locationList.add(element.value)
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
            if (exerciseClient.getCurrentExerciseInfo().exerciseTrackedStatus != ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS){
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
        bpm_counter = 0
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




    fun sendHRToHandheld(){
        /*
        salji listu BPM, listu lokacija, totalCals, totalDistance
         */

        val dataMapRequest = PutDataMapRequest.create("/heartrate/${Instant.now().epochSecond}").apply{
            dataMap.putByteArray("heartrate",Json.encodeToString(BPMList).toByteArray())
            BPMList = mutableListOf()
        }
        val putDataRequest = dataMapRequest.asPutDataRequest().setUrgent()
        dataClient.putDataItem(putDataRequest)
        .addOnSuccessListener { dataItem->
            Log.d("DataClient", "DataItem saved: $dataItem")
        }
        .addOnFailureListener { exception->
            Log.d("DataClient","Failed to send: $exception")
        }

    }
    fun sendLocationToHandheld(){
        val dataMapRequest = PutDataMapRequest.create("/location/${Instant.now().epochSecond}").apply{
            dataMap.putByteArray("location",Json.encodeToString(locationList).toByteArray())
            locationList = mutableListOf()
        }
        val putDataRequest = dataMapRequest.asPutDataRequest().setUrgent()
        dataClient.putDataItem(putDataRequest)
            .addOnSuccessListener { dataItem->
                Log.d("DataClient", "DataItem saved: $dataItem")
            }
            .addOnFailureListener { exception->
                Log.d("DataClient","Failed to send: $exception")
            }
    }

    fun sendDistance(){
        val dataMapRequest = PutDataMapRequest.create("/distance").apply{
            dataMap.putInt("distance",_distance.value.toInt())
        }
        val putDataRequest = dataMapRequest.asPutDataRequest().setUrgent()
        dataClient.putDataItem(putDataRequest)
            .addOnSuccessListener { dataItem->
                Log.d("DataClient", "DataItem saved: $dataItem")
            }
            .addOnFailureListener { exception->
                Log.d("DataClient","Failed to send: $exception")
            }
    }

    fun sendCalories(){
        val dataMapRequest = PutDataMapRequest.create("/calories").apply{
            dataMap.putInt("calories",_calories.value.toInt())
        }
        val putDataRequest = dataMapRequest.asPutDataRequest().setUrgent()
        dataClient.putDataItem(putDataRequest)
            .addOnSuccessListener { dataItem->
                Log.d("DataClient", "DataItem saved: $dataItem")
            }
            .addOnFailureListener { exception->
                Log.d("DataClient","Failed to send: $exception")
            }
    }


}

fun printEnded(ended: Boolean, reason: Int){
    if (ended){
        Log.d("Exercise Update","Exercise ended: $ended")
        println("REASON :")
        println(
            when(reason){
                ExerciseEndReason.UNKNOWN -> "Unknown"
                ExerciseEndReason.USER_END -> "USER_END"
                ExerciseEndReason.AUTO_END_SUPERSEDED -> "AUTO_END_SUPERSEDED"
                ExerciseEndReason.AUTO_END_PAUSE_EXPIRED -> "AUTO_END_PAUSE_EXPIRED"
                ExerciseEndReason.AUTO_END_PERMISSION_LOST -> "AUTO_END_PERMISSION_LOST"
                ExerciseEndReason.AUTO_END_MISSING_LISTENER -> "AUTO_END_MISSING_LISTENER"
                ExerciseEndReason.AUTO_END_PREPARE_EXPIRED -> "AUTO_END_PREPARE_EXPIRED"
                else -> ""
            }
        )
    }
}