package com.example.fitnessapp.services

import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.example.fitnessapp.data.database.entities.Workout
import com.example.fitnessapp.presentation.AVG_BPM
import com.example.fitnessapp.presentation.AVG_CAL
import com.example.fitnessapp.presentation.AVG_DIST
import com.example.fitnessapp.presentation.AVG_LEN
import com.example.fitnessapp.presentation.CALS_GOAL
import com.example.fitnessapp.presentation.NUM_CARDIO
import com.example.fitnessapp.presentation.NUM_WORKOUTS
import com.example.fitnessapp.presentation.STEPS_GOAL
import com.example.fitnessapp.presentation.dataStore
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import com.example.fitnessapp.repositories.WorkoutRepository
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer
import javax.inject.Inject

@AndroidEntryPoint
class PhoneMessageListenerService @Inject constructor()
    : WearableListenerService() {

    var message = ""

    @Inject
    lateinit var repository: WorkoutRepository


    override fun onMessageReceived(messageEvent: MessageEvent) {
        println("MESSAGE RECIEVED")

        if (messageEvent.path == "/testResponse"){
            Log.d("MESSAGE RECEIVED", messageEvent.data.toString())
        }

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("STARTED SERVICE")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        println("CREATED SERVICE")

        super.onCreate()
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        //println("DATA CHANGED")
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                println("DATA CHANGED, ${event.dataItem.uri.path}")

                val dataItem = event.dataItem
               // println(dataItem.uri.path)
                if (dataItem.uri.path == null) return

                val segments = dataItem.uri.path?.split("/") ?: return
                val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                val path = segments[1]
                if (path == "steps_goal"){
                    CoroutineScope(Dispatchers.IO).launch {
                        this@PhoneMessageListenerService.dataStore.edit { preferences ->
                            preferences[STEPS_GOAL] = dataMap.getInt("steps_goal")
                        }
                    }
                }

                if (path == "cals_goal"){
                    CoroutineScope(Dispatchers.IO).launch {
                        this@PhoneMessageListenerService.dataStore.edit { preferences ->
                            preferences[CALS_GOAL] = dataMap.getInt("cals_goal")
                        }
                    }
                }

                if (path == "workout_labels"){
                    val labels: LabelList =
                        Json.decodeFromString(dataMap.getByteArray("workout_labels")!!.toString(Charsets.UTF_8))

                    CoroutineScope(Dispatchers.IO).launch {
                        repository.clearLabels()
                        for (label in labels.list){
                            repository.insert(Workout(label = label.label,
                                workoutType = label.workoutType,
                                color = label.color))
                        }
                    }
                }


                if (path == "simple_stats"){

                    val stats: SimpleStats = Json.decodeFromString(dataMap.getByteArray(path)!!.toString(Charsets.UTF_8))

                    CoroutineScope(Dispatchers.IO).launch {
                        this@PhoneMessageListenerService.dataStore.edit { preferences ->
                            preferences[NUM_WORKOUTS] = stats.numWorkouts
                            preferences[NUM_CARDIO] = stats.numCardio
                            preferences[AVG_CAL] = stats.calories
                            preferences[AVG_LEN] = stats.length
                            preferences[AVG_BPM] = stats.bpm
                            preferences[AVG_DIST] = stats.distance
                        }
                    }

                    //println("received: $stats")
                }


            }
        }

    }
}

@Serializable
data class LabelList(
    val list: List<WorkoutLabel> = listOf()
)

@Serializable
data class WorkoutLabel(
    val label: String = "",
    val workoutType: WorkoutType = WorkoutType.GYM,
    val color: Int = 0
)

@Serializable
data class SimpleStats(
    val length: Long,
    val calories: Long,
    val numWorkouts: Long,
    val numCardio: Long,
    val distance: Long,
    val bpm: Long
)