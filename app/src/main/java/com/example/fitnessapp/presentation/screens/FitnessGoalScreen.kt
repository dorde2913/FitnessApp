package com.example.fitnessapp.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn

@Composable
fun FitnessGoalScreen(modifier: Modifier = Modifier){
    ScalingLazyColumn {
        item{
            Text("FITNESS GOAL SCREEN :D", modifier = Modifier.padding(30.dp), color = Color.White)
        }
    }
}