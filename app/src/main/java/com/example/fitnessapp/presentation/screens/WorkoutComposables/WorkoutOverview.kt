package com.example.fitnessapp.presentation.screens.WorkoutComposables

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnessapp.R
import com.example.fitnessapp.database.entities.Workout
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import com.example.fitnessapp.presentation.stateholders.WorkoutViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("DefaultLocale")
@Composable
fun WorkoutOverview(viewModel: WorkoutViewModel){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val workout by viewModel.getWorkoutByLabel(uiState.workoutLabel).collectAsStateWithLifecycle(initialValue = Workout())

    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS")

    val backgroundColor = Color(workout.color).darken(0.8f)
    val textColor = backgroundColor.getContrastingColor()

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor),
        contentAlignment = Alignment.Center){


        Icon(painterResource(
            if (workout.workoutType == WorkoutType.GYM)
                R.drawable.jimjpeg_removebg_preview
            else
                R.drawable.running1_removebg_preview
        ),null,
            tint = backgroundColor.contrast(),
            modifier = Modifier.size(if (workout.workoutType == WorkoutType.CARDIO) 150.dp else 400.dp))

        Column(modifier = Modifier.fillMaxSize().background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){

            Spacer(modifier = Modifier.height(50.dp))
            Text("Workout overview", color = textColor, fontSize = 20.sp, textAlign = TextAlign.Center
                , modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(painterResource(R.drawable.stopwatchicon_removebg_preview),null,tint = textColor,
                    modifier = Modifier.size(20.dp))
                Text(
                    LocalTime.ofNanoOfDay(uiState.workoutLength * 1_000_000).format(formatter), color = textColor,
                    fontSize = 20.sp, textAlign = TextAlign.Center)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(painterResource(R.drawable.caloriesicon_removebg_preview),null, tint = textColor,
                    modifier = Modifier.size(20.dp))
                Text("calories: ${String.format("%.2f",uiState.totalCals)}", color = textColor, fontSize = 20.sp,
                    textAlign = TextAlign.Center)
            }

            if (workout.workoutType == WorkoutType.CARDIO){
                /*
                Ovde idu prikazi za distance i average speed npr
                 */
            }

        }
    }

}
fun Color.getContrastingColor(): Color {
    return if (this.luminance() > 0.5) Color.Black else Color.White
}

fun Color.contrast():Color{
    return if(this.luminance() > 0.5) this.darken(0.4f) else this.lighten(0.2f)
}