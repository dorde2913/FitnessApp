package com.example.fitnessapp.data.handheld

import android.content.Context
import android.util.Log
import com.example.fitnessapp.presentation.dataStore

import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.contracts.contract


/*
class for communication with handheld companion app
 */

@Singleton
class HandheldClient @Inject constructor(
    @ApplicationContext val context: Context
) {


    private val dataClient by lazy { Wearable.getDataClient(context)}



    fun sendHRToHandheld(timestamp: Long,
                         BPMList: List<Int>,
                         currentLabel: String,
                         length: Long){
        /*
        salji listu BPM, listu lokacija, totalCals, totalDistance
         */

        val dataMapRequest = PutDataMapRequest.create("/heartrate/" +
                "${Instant.now().epochSecond}").apply{
            dataMap.putByteArray("heartrate", Json.encodeToString(
                BPMUpdate(id = timestamp, BPMList = BPMList, label = currentLabel, length = length)
            ).toByteArray())
            //BPMList = mutableListOf() ovo treba nakon poziva
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
    fun sendLocationToHandheld(timestamp: Long,
                               locationList: List<Pair<Double,Double>>,
                               currentLabel: String){
        val dataMapRequest = PutDataMapRequest.create("/location/" +
                "${Instant.now().epochSecond}").apply{
            dataMap.putByteArray("location", Json.encodeToString(
                LocationUpdate(id = timestamp, locationList = locationList, label = currentLabel)
            ).toByteArray())
            //locationList = mutableListOf()
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

    fun sendDistance(timestamp: Long,
                     distance: Int,
                     currentLabel: String){
        val dataMapRequest = PutDataMapRequest.create("/distance").apply{
            dataMap.putByteArray("distance", Json.encodeToString(
                DistanceUpdate(id = timestamp, distance = distance, label = currentLabel)
            ).toByteArray())
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

    fun sendDailyHR(list: List<Int>) {
        val dataMapRequest = PutDataMapRequest.create("/daily_heartrate").apply{
            dataMap.putByteArray("daily_heartrate", Json.encodeToString(
                DailyHeartRateUpdate(hrList = list)
            ).toByteArray())
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


    fun sendCalories(timestamp: Long,
                     calories: Int,
                     currentLabel: String){
        val dataMapRequest = PutDataMapRequest.create("/calories").apply{
            dataMap.putByteArray("calories", Json.encodeToString(
                CaloriesUpdate(id = timestamp, calories = calories, label = currentLabel)
            ).toByteArray())
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




    fun sendCaloriesDaily(calories: Int){
        val dataMapRequest = PutDataMapRequest.create("/calories_daily").apply{
            dataMap.putByteArray("calories_daily",
                Json.encodeToString(DailyCaloriesUpdate(calories = calories)).toByteArray())
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

    fun sendSteps(steps: Int){
        val dataMapRequest = PutDataMapRequest.create("/steps_daily").apply{
            dataMap.putByteArray("steps_daily",
                Json.encodeToString(DailyStepsUpdate(steps = steps)).toByteArray())
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

