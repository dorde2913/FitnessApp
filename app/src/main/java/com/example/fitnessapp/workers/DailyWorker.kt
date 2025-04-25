package com.example.fitnessapp.workers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fitnessapp.data.handheld.HandheldClient
import com.example.fitnessapp.presentation.DAILY_LEN
import com.example.fitnessapp.presentation.MAX_KEY
import com.example.fitnessapp.presentation.MIN_KEY
import com.example.fitnessapp.presentation.dataStore
import com.example.fitnessapp.repositories.PassiveMonitoringRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    @Assisted val repository: PassiveMonitoringRepository,
    @Assisted val handheldClient: HandheldClient
): CoroutineWorker(context, workerParams){




    override suspend fun doWork(): Result {
        //ovde cemo da resetujemo sve vrednosti


        println("WORKER WORKIN")

        context.dataStore.edit { preferences->
            preferences[MAX_KEY] = 0
            preferences[MIN_KEY] = 0
            preferences[DAILY_LEN] = 0
        }

        for (i in 0 until 24){
            repository.setMaxHr(i,0)
        }

        handheldClient.sendDailyHR(repository.getHRMaxesNoFlow().map{it.value})


        return Result.success()
    }
}