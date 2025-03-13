package com.example.fitnessapp.services

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import java.nio.ByteBuffer
import javax.inject.Inject

@AndroidEntryPoint
class PhoneMessageListenerService @Inject constructor()
    : WearableListenerService() {

    var message = ""


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
//        dataEvents.forEach { event ->
//            if (event.type == DataEvent.TYPE_DELETED) {
//
//                val dataItem = event.dataItem
//                if (dataItem.uri.path?.endsWith("/heartrate") == true) {
//                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
//                    val heartRate = dataMap.getInt("heartrate")
//                    Log.d("DataLayerListenerService", "New heart rate value received: $heartRate")
//                }
//            }
//        }
    }
}