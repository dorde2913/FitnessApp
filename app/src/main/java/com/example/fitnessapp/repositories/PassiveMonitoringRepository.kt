package com.example.fitnessapp.repositories

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.PassiveListenerCallback
import androidx.health.services.client.PassiveMonitoringClient
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class PassiveMonitoringRepository @Inject constructor(
    @ApplicationContext context: Context
){
    val healthClient: HealthServicesClient = HealthServices.getClient(context)
    val passiveMonitoringClient: PassiveMonitoringClient = healthClient.passiveMonitoringClient



    init {
        passiveMonitoringClient.setPassiveListenerCallback(
            PassiveListenerConfig.builder()
                .setDataTypes(setOf(DataType.STEPS, DataType.HEART_RATE_BPM))
                .build()
            ,
            object: PassiveListenerCallback {
                override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
                    super.onNewDataPointsReceived(dataPoints)

                    for (dataPoint in dataPoints.getData(DataType.STEPS)){
                        println("STEPS: ${dataPoint.value}")
                    }
                    for (dataPoint in dataPoints.getData(DataType.HEART_RATE_BPM)){
                        println("STEPS: ${dataPoint.value}")
                    }
                }
            })
    }

}