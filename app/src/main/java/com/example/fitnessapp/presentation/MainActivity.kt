/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.fitnessapp.presentation


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.compose.foundation.layout.fillMaxSize


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

import androidx.compose.ui.tooling.preview.Preview

import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.PassiveListenerCallback
import androidx.health.services.client.PassiveMonitoringClient
import androidx.health.services.client.data.ComparisonType
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeCondition
import androidx.health.services.client.data.PassiveGoal
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState

import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.tooling.preview.devices.WearDevices
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
import com.example.fitnessapp.presentation.stateholders.WorkoutViewModel
import com.example.fitnessapp.presentation.theme.FitnessAppTheme
import com.example.fitnessapp.services.FitService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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

@Composable
fun WearApp(context: Context) {

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


    val workoutViewModel: WorkoutViewModel = viewModel()
    //val view = LocalView.current
   //view.keepScreenOn = true

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermissionGranted ->
        //start servisa ili stagod
    }


    val healthClient: HealthServicesClient = HealthServices.getClient(context)
    val passiveMonitoringClient: PassiveMonitoringClient = healthClient.passiveMonitoringClient

    var steps by rememberSaveable { mutableStateOf(0) }

    val dailyStepsGoal by lazy {
        val condition = DataTypeCondition(
            dataType = DataType.STEPS_DAILY,
            threshold = 1_000, // Trigger every 10000 steps
            comparisonType = ComparisonType.GREATER_THAN_OR_EQUAL
        )
        PassiveGoal(condition)
    }

    passiveMonitoringClient.setPassiveListenerCallback(
        PassiveListenerConfig.builder()
            .setDataTypes(setOf(DataType.STEPS, DataType.HEART_RATE_BPM))
            .setDailyGoals(setOf(dailyStepsGoal))
            .build()
        ,
        object: PassiveListenerCallback {

            override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
                super.onNewDataPointsReceived(dataPoints)
                println("BUH BUH NEW DATA BUH BUH")

                for (dataPoint in dataPoints.getData(DataType.STEPS)){
                    steps = dataPoint.value.toInt()
                    println("STEPS: ${dataPoint.value}")
                }
                for (dataPoint in dataPoints.getData(DataType.HEART_RATE_BPM)){
                    println("STEPS: ${dataPoint.value}")
                }
            }
    })

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
                enterTransition = {->
                    if (this.initialState.destination.route == DestinationHome.route
                        || this.targetState.destination.route == DestinationWorkoutOngoing.route){
                        slideInHorizontally(
                            initialOffsetX = {it}
                        )
                    }
                    else{
                        slideInHorizontally(
                            initialOffsetX = {-it}
                        )
                    }
                },
                exitTransition = {
                    if (this.initialState.destination.route == DestinationHome.route
                        || this.targetState.destination.route == DestinationWorkoutOngoing.route){
                        slideOutHorizontally(
                            targetOffsetX = {-it}
                        )+ fadeOut()
                    }
                    else{
                        slideOutHorizontally(
                            targetOffsetX = {it}
                        ) + fadeOut()
                    }
                }
            ) {
                composable(route = DestinationHome.route){
                    HomeScreen(onChipClick = {route -> navController.navigate(route)}, listState = listState)
                }
                composable(route = DestinationDaily.route){
                   DailyActivityScreen()
                }
                composable(route = DestinationGoals.route){
                    FitnessGoalScreen()
                }
                composable(route = DestinationWorkout.route){
                    WorkoutScreen(viewModel = workoutViewModel, navigateToOngoing = {navController.navigate(
                        DestinationWorkoutOngoing.route)})
                }
                composable(route = DestinationHealth.route){
                    HealthScreen()
                }
                composable(route = DestinationSettings.route){
                    SettingScreen(context=context)
                }
                composable(route = DestinationWorkoutOngoing.route){
                    OngoingWorkout(viewModel = workoutViewModel,
                        {
                            navController.navigate(DestinationWorkoutOverview.route){
                                popUpTo(DestinationHome.route){
                                    saveState = false
                                }
                            }
                        })
                }
                composable(route = DestinationWorkoutOverview.route){
                    WorkoutOverview(viewModel = workoutViewModel)
                }
            }


        }
    }
}

