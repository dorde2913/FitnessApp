/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.fitnessapp.presentation


import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.Manifest.permission.BODY_SENSORS
import android.Manifest.permission.BODY_SENSORS_BACKGROUND
import android.Manifest.permission.FOREGROUND_SERVICE_HEALTH
import android.Manifest.permission.FOREGROUND_SERVICE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import android.provider.Settings
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button

import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.dialog.Confirmation
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fitnessapp.presentation.screens.DailyActivityScreen
import com.example.fitnessapp.presentation.screens.DestinationDaily
import com.example.fitnessapp.presentation.screens.DestinationGoals
import com.example.fitnessapp.presentation.screens.DestinationHealth
import com.example.fitnessapp.presentation.screens.DestinationHome
import com.example.fitnessapp.presentation.screens.DestinationSettings
import com.example.fitnessapp.presentation.screens.DestinationWorkout
import com.example.fitnessapp.presentation.screens.DestinationWorkoutOngoing
import com.example.fitnessapp.presentation.screens.DestinationWorkoutOverview
import com.example.fitnessapp.presentation.screens.FitnessGoalScreen
import com.example.fitnessapp.presentation.screens.HealthScreen
import com.example.fitnessapp.presentation.screens.HomeScreen
import com.example.fitnessapp.presentation.screens.SettingScreen
import com.example.fitnessapp.presentation.screens.WorkoutComposables.OngoingWorkout
import com.example.fitnessapp.presentation.screens.WorkoutComposables.WorkoutOverview
import com.example.fitnessapp.presentation.screens.WorkoutComposables.WorkoutScreen
import com.example.fitnessapp.presentation.stateholders.PassiveViewModel
import com.example.fitnessapp.presentation.stateholders.WorkoutViewModel
import com.example.fitnessapp.presentation.theme.FitnessAppTheme
import com.example.fitnessapp.repositories.PassiveMonitoringRepository
import com.example.fitnessapp.workers.DailyWorker
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.concurrent.TimeUnit

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "passive_stats")
val MIN_KEY = intPreferencesKey("daily_min")
val MAX_KEY = intPreferencesKey("daily_max")
val DAILY_LEN = longPreferencesKey("daily_len")

val AVG_LEN = longPreferencesKey("avg_len")
val AVG_BPM = longPreferencesKey("avg_bpm")
val AVG_CAL = longPreferencesKey("avg_cal")
val NUM_WORKOUTS = longPreferencesKey("num_workouts")



fun getInitialDelay(): Long{
    val now = Calendar.getInstance()
    val nextMidnight = Calendar.getInstance().apply {
        // Set time to midnight (00:00:00.000)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        // If midnight has passed today, schedule for tomorrow
        if (before(now)) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    return nextMidnight.timeInMillis - now.timeInMillis
}

fun scheduleWork(context: Context){
    val initialDelay = getInitialDelay()
    val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyWorker>(1,TimeUnit.DAYS)
        .setInitialDelay(initialDelay,TimeUnit.MILLISECONDS)
        .build()

//    val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyWorker>()
//        .build()

    val operation = WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "DailyWorker",
        ExistingPeriodicWorkPolicy.UPDATE,
        dailyWorkRequest
    )


    println("SCHEDULED  ${operation.state.value}")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        println("ONCREATE")
        installSplashScreen()



        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)



        setContent {
            WearApp(this)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WearApp(context: Context) {


    val workoutViewModel: WorkoutViewModel = viewModel()
    val passiveViewModel: PassiveViewModel = viewModel()


    var checked by rememberSaveable { mutableStateOf(
        context.getSharedPreferences("screenAlwaysOn", Context.MODE_PRIVATE).getBoolean("screenAlwaysOn",false)
    ) }
    val sharedPref = context.getSharedPreferences("screenAlwaysOn", Context.MODE_PRIVATE)
        .registerOnSharedPreferenceChangeListener { sharedPreferences, s ->
            checked = sharedPreferences.getBoolean("screenAlwaysOn",false)
        }

    val view = LocalView.current
    DisposableEffect(checked) {

        view.keepScreenOn = checked
        onDispose {}

    }

    val hour = Instant.ofEpochSecond(Instant.now().epochSecond).atZone(
        ZoneId.systemDefault()).hour


    val packageInfo = context.packageManager.getPackageInfo(
        context.packageName,
        PackageManager.GET_PERMISSIONS
    )

    val req_perms = packageInfo.requestedPermissions ?: emptyArray()

    val required_perms = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION,
        POST_NOTIFICATIONS,
        BODY_SENSORS,
        ACTIVITY_RECOGNITION
    )

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {perms ->

        }
    )
    LaunchedEffect(Unit) {
        passiveViewModel.subscribe()
        scheduleWork(context)
        passiveViewModel.clearHRMaxes(hour)
        println("REQUIRED PERMS")
        for (perm in req_perms){
            println(perm)
        }

    }



    val dialogQueue = mainViewModel.visiblePermissionDialogQueue
    val navController = rememberNavController()

    val listState = rememberScalingLazyListState()
    FitnessAppTheme {
        Scaffold (
            vignette = {
                Vignette(vignettePosition = VignettePosition.TopAndBottom)
            },
            //timeText = { if (navController.currentDestination?.route != DestinationWorkout.route) TimeText()},
            positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
        ) {

            NavHost(
                modifier = Modifier
                    .fillMaxSize(),
                navController = navController,
                startDestination = DestinationHome.route,
                enterTransition = { ->
                    if (this.initialState.destination.route == DestinationHome.route
                        || this.targetState.destination.route == DestinationWorkoutOngoing.route
                    ) {
                        slideInHorizontally(
                            initialOffsetX = { it }
                        )
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { -it }
                        )
                    }
                },
                exitTransition = {
                    if (this.initialState.destination.route == DestinationHome.route
                        || this.targetState.destination.route == DestinationWorkoutOngoing.route
                    ) {
                        slideOutHorizontally(
                            targetOffsetX = { -it }
                        ) + fadeOut()
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { it }
                        ) + fadeOut()
                    }
                }
            ) {
                composable(route = DestinationHome.route) {
                    HomeScreen(
                        onChipClick = { route ->

                            if (route == DestinationSettings.route)
                                navController.navigate(route)
                            else{
                                //msm da ne mora else ali stagod
                                if (!required_perms.all {
                                        ContextCompat.checkSelfPermission(
                                            context, it
                                        ) == PackageManager.PERMISSION_GRANTED
                                    }
                                ) {
                                    //handle if double declined / perma declined
                                    multiplePermissionResultLauncher.launch(required_perms)
                                } else {
                                    navController.navigate(route)
                                }
                            }
                            //navController.navigate(route)
                          },
                        listState = listState
                    )
                }
                composable(route = DestinationDaily.route) {
                    DailyActivityScreen(viewModel = passiveViewModel)
                }
                composable(route = DestinationGoals.route) {
                    FitnessGoalScreen()
                }
                composable(route = DestinationWorkout.route) {
                    WorkoutScreen(viewModel = workoutViewModel, navigateToOngoing = {
                        navController.navigate(
                            DestinationWorkoutOngoing.route
                        )
                    })
                }
                composable(route = DestinationHealth.route) {
                    HealthScreen(viewModel = passiveViewModel)
                }
                composable(route = DestinationSettings.route) {
                    SettingScreen(context = context)
                }
                composable(route = DestinationWorkoutOngoing.route) {
                    OngoingWorkout(viewModel = workoutViewModel,
                        {
                            navController.navigate(DestinationWorkoutOverview.route) {
                                popUpTo(DestinationHome.route) {
                                    saveState = false
                                }
                            }
                        })
                }
                composable(route = DestinationWorkoutOverview.route) {
                    WorkoutOverview(viewModel = workoutViewModel)
                }
            }




        }
    }

}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}