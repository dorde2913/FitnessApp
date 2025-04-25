package com.example.fitnessapp.broadcastreceivers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import com.example.fitnessapp.presentation.scheduleWork
import com.example.fitnessapp.repositories.PassiveMonitoringRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    //@Inject
   // lateinit var repository: PassiveMonitoringRepository

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED){
            println("ACTION BOOT COMPLETED :D")
            //this might not be necessary
            val repository = getRepository(context)
            CoroutineScope(Dispatchers.Main).launch {
                repository.subscribe()
                scheduleWork(context)
            }
        }
    }

    private fun getRepository(context: Context): PassiveMonitoringRepository {

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            PassiveMonitoringRepositoryEntryPoint::class.java
        )
        return entryPoint.repository
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PassiveMonitoringRepositoryEntryPoint {
        val repository: PassiveMonitoringRepository
    }
}