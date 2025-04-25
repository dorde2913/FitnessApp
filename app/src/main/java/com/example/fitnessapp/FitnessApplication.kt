package com.example.fitnessapp


import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.fitnessapp.data.handheld.HandheldClient
import com.example.fitnessapp.data.handheld.HandheldDataListener
import com.example.fitnessapp.repositories.PassiveMonitoringRepository
import com.example.fitnessapp.workers.DailyWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class FitnessApplication: Application(), Configuration.Provider{
    @Inject
    lateinit var workerFactory: DailyWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


}


class DailyWorkerFactory @Inject constructor(private val repository: PassiveMonitoringRepository,
    private val handheldClient: HandheldClient,
    val handheldDataListener: HandheldDataListener)://this listener is here so it would get initialized
    WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = DailyWorker(repository = repository, context = appContext, workerParams = workerParameters,
        handheldClient = handheldClient)


}