package com.example.fitnessapp.workers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fitnessapp.presentation.DAILY_LEN
import com.example.fitnessapp.presentation.MAX_KEY
import com.example.fitnessapp.presentation.MIN_KEY
import com.example.fitnessapp.presentation.dataStore
import com.example.fitnessapp.repositories.PassiveMonitoringRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Thread.sleep
import javax.inject.Inject

@HiltWorker
class DailyWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
): CoroutineWorker(context, workerParams){



    override suspend fun doWork(): Result {
        //ovde cemo da resetujemo sve vrednosti

        println("WORKER WORKIN")

        context.dataStore.edit { preferences->
            preferences[MAX_KEY] = 0
            preferences[MIN_KEY] = 0
            preferences[DAILY_LEN] = 0

        }


        return Result.success()
    }
}