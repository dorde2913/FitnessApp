package com.example.fitnessapp.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.coroutineScope
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.MainActivity
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import com.example.fitnessapp.repositories.ExerciseClientRepository
import com.example.fitnessapp.repositories.PassiveMonitoringRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FitService @Inject constructor()
: LifecycleService() {
//repo mora kao lateinit var
    companion object{
        private val className = FitService::class.java.simpleName

        const val NOTIFICATION_CHANNEL_ID = "fitness-app-notification-channel"
        const val NOTIFICATION_ID = 33
    }

    @Inject @ApplicationContext lateinit var context: Context
    @Inject lateinit var exerciseClientRepository: ExerciseClientRepository

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {



        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:WakeLock")
        wakeLock.acquire(10*60*1000L /*10 minutes*/)

        Log.d(className, "onStartCommand()")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, getNotification())
        //val notificationManager = context.getSystemService(NotificationManager::class.java)
        //notificationManager.notify(NOTIFICATION_ID,getNotification())

        exerciseClientRepository.startExercise()

        lifecycle.coroutineScope.launch {
            while(true){
                Log.d(className,"buh!")
                exerciseClientRepository.sendHRToHandheld()
                exerciseClientRepository.sendCalories()

                if (exerciseClientRepository.currentType == WorkoutType.CARDIO){
                    exerciseClientRepository.sendDistance()
                    exerciseClientRepository.sendLocationToHandheld()
                }


                delay(1000 * 60 )//a minute
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(className, "onCreate()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(className, "onDestroy()")
        exerciseClientRepository.endExercise()

        exerciseClientRepository.sendHRToHandheld()
        exerciseClientRepository.sendCalories()

        if (exerciseClientRepository.currentType == WorkoutType.CARDIO){
            exerciseClientRepository.sendDistance()
            exerciseClientRepository.sendLocationToHandheld()
        }

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:WakeLock")
        if (wakeLock.isHeld)wakeLock.release()
    }

    private fun createNotificationChannel() {
        // https://developer.android.com/develop/ui/views/notifications/build-notification
        val notificationChannel = NotificationChannelCompat
            .Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_MAX)
            .setName("General channel")
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)
    }


    private fun getNotification(): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // https://developer.android.com/develop/ui/views/notifications/build-notification
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle("FitApp")
            setContentText("Workout in progress")
            setContentIntent(pendingIntent)
            setSmallIcon(R.drawable.caloriesicon_removebg_preview)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                foregroundServiceBehavior = Notification.FOREGROUND_SERVICE_IMMEDIATE
            }
        }
        return notificationBuilder.build()
    }



}