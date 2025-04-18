package com.example.fitnessapp.services

import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.example.fitnessapp.data.database.entities.Workout
import com.example.fitnessapp.presentation.CALS_GOAL
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

    fun ByteArray.toDoubleList(): List<Double> {
        val buffer = ByteBuffer.wrap(this)
        val list = mutableListOf<Double>()
        while (buffer.hasRemaining()) {
            list.add(buffer.double)
        }
        return list
    }

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
        println("DATA CHANGED")
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                println("DATA CHANGED")

                val dataItem = event.dataItem
                println(dataItem.uri.path)
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