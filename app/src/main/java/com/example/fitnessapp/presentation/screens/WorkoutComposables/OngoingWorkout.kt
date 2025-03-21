package com.example.fitnessapp.presentation.screens.WorkoutComposables

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults

import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import com.example.fitnessapp.R
import com.example.fitnessapp.data.database.entities.Workout
import com.example.fitnessapp.presentation.stateholders.TimerState
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import com.example.fitnessapp.presentation.stateholders.WorkoutViewModel

@SuppressLint("DefaultLocale")
@Composable
fun OngoingWorkout(viewModel: WorkoutViewModel, navigateToOverview: ()->Unit){

    var showOverlay by rememberSaveable { mutableStateOf(false) }
    BackHandler {
        showOverlay = !showOverlay
    }

    val timer by viewModel.stopWatchText.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val calories by viewModel.totalCals.collectAsState()
    val speed by viewModel.speed.collectAsState()
    val distance by viewModel.distance.collectAsState()

    val uiState by viewModel.uiState.collectAsState()
    //val timerState by viewModel.timerState.collectAsState()

    val workout by viewModel.getWorkoutByLabel(uiState.workoutLabel).collectAsStateWithLifecycle(initialValue = Workout())
    //if (workout!=null)



    LaunchedEffect(Unit) {
        viewModel.toggleIsRunning()
        viewModel.startExercise()
    }


    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(workout.color).darken(0.7f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(Modifier.height(20.dp))
            OngoingWorkoutChip(uiState.workoutLabel, cardHeight = 30, middleText = true)
            OngoingWorkoutChip(timer,R.drawable.stopwatchicon_removebg_preview, textSize = 30, iconSize = 20)

            Column(
                modifier = Modifier.verticalScroll(
                    state = rememberScrollState(),
                ).fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                OngoingWorkoutChip("${heartRate}bpm",R.drawable.heartrateicon_removebg_preview, iconSize = 20)
                OngoingWorkoutChip("${String.format("%.2f",calories)}kcal",R.drawable.caloriesicon_removebg_preview, iconSize = 20)

                val distanceKM = distance.toInt()/1000
                val distanceMeter = distance.toInt()%1000

                if (workout.workoutType == WorkoutType.CARDIO){
                    OngoingWorkoutChip(
                        label = "Current speed: ${String.format("%.2f",speed)} m/s"
                    )
                    OngoingWorkoutChip(
                        label = if (distanceKM>0) "Distance: ${distanceKM}km ${distanceMeter}m"
                        else "Distance: $distanceMeter"
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }

        }
        AnimatedVisibility(
            visible = showOverlay,
            enter = slideInHorizontally(
                initialOffsetX = {-it}
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it }
            )
        ) {
            OngoingExerciseOverlay(viewModel,navigateToOverview)
        }
    }

}
@Composable
fun OngoingExerciseOverlay(viewModel: WorkoutViewModel, navigateToOverview: () -> Unit){
    val timerState by viewModel.timerState.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ){

                IconButton(
                    onClick = {
                        viewModel.toggleIsRunning()
                        viewModel.finishExercise()
                        viewModel.resetTimer()
                        navigateToOverview()
                    },
                    modifier = Modifier.clip(CircleShape).background(Color.Red.darken(0.3f))
                ) {
                    Icon(Icons.Filled.Close,null)
                }


                IconButton(
                    onClick = {
                        //pause/unpause workout
                        viewModel.toggleIsRunning()
                        if (timerState == TimerState.RUNNING) viewModel.pauseExercise()
                        else viewModel.resumeExercise()
                    },
                    modifier = Modifier.clip(CircleShape).background(if (timerState == TimerState.RUNNING)Color.Gray else Color.Green.darken(0.5f))
                ) {
                    if (timerState == TimerState.RUNNING)
                        Icon(painterResource(R.drawable.pauseiconicon_removebg_preview),null)
                    else Icon(Icons.Default.PlayArrow,null)
                }

            }
        }
    }
}

@Composable
fun OngoingWorkoutChip(label: String, icon: Int? = null,iconSize: Int?=null, borderColor: Color?=null,
                       textSize: Int = 15,cardHeight: Int = 45, minWidth: Int = 0, middleText: Boolean = false){
    Chip(
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colors.surface,
            disabledBackgroundColor = MaterialTheme.colors.surface
        )
        ,
        onClick = {
            //settings
        },
        enabled = false,
        label = {
            Text(text = label, color = MaterialTheme.colors.onSurface, fontSize = textSize.sp, textAlign =if (middleText)TextAlign.Center else TextAlign.Right,
                modifier = Modifier.fillMaxWidth())
        },
        modifier = Modifier
            .padding(vertical = 2.dp)
            .height(cardHeight.dp)
            .widthIn(min = 130.dp),
        icon = {
            if (icon!=null && iconSize!=null)
            Icon(painterResource(icon) ,
                null,
                modifier = Modifier.size(iconSize.dp))
        },

        border = ChipDefaults.chipBorder(
            borderStroke = if (borderColor!=null) BorderStroke(width = 3.dp, color = borderColor) else null
        )
    )
}
