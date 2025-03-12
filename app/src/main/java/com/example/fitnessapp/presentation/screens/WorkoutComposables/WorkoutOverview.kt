package com.example.fitnessapp.presentation.screens.WorkoutComposables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnessapp.database.entities.Workout
import com.example.fitnessapp.presentation.stateholders.WorkoutViewModel

@Composable
fun WorkoutOverview(viewModel: WorkoutViewModel){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val workout by viewModel.getWorkoutByLabel(uiState.workoutLabel).collectAsStateWithLifecycle(initialValue = Workout())

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("WOrkout overview", color = Color.White, fontSize = 30.sp)
    }
}