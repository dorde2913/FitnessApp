package com.example.fitnessapp.data.handheld

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.fitnessapp.presentation.DAILY_CALS
import com.example.fitnessapp.presentation.DAILY_STEPS
import com.example.fitnessapp.presentation.dataStore
import com.example.fitnessapp.repositories.ExerciseClientRepository
import com.example.fitnessapp.repositories.PassiveMonitoringRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HandheldDataListener @Inject constructor(
    @ApplicationContext val context: Context,
    passiveMonitoringRepository: PassiveMonitoringRepository,
    val handheldClient: HandheldClient
) {
    val dailyHR = passiveMonitoringRepository.hrMaxes

    /*
    These values can be changed in different parts of the code, so this listener serves to update the
    handheld device automatically in order to avoid manually updating it in every part of the code where the values change
     */
    init{

        CoroutineScope(Dispatchers.IO).launch {
            dailyHR.collect{hrList ->
                handheldClient.sendDailyHR(hrList.map{it.value})
            }
        }

        CoroutineScope(Dispatchers.IO).launch {

            context.dataStore.data.collect{preferences ->
                handheldClient.sendSteps(preferences[DAILY_STEPS]?:0)
                handheldClient.sendCaloriesDaily(preferences[DAILY_CALS]?:0)
            }
        }

    }

}