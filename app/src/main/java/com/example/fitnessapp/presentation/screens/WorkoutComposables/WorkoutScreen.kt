package com.example.fitnessapp.presentation.screens.WorkoutComposables


import androidx.compose.foundation.background


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.MaterialTheme

import com.example.fitnessapp.presentation.stateholders.WorkoutState
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import com.example.fitnessapp.presentation.stateholders.WorkoutViewModel

@Composable
fun WorkoutScreen(modifier: Modifier = Modifier, viewModel: WorkoutViewModel
                  , navigateToOngoing: ()->Unit ){

    val uiState by viewModel.uiState.collectAsState()
    val pageState = rememberPagerState(pageCount = {2})

    VerticalPager(
        state = pageState,
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {index ->
        //if not ongoingWorkout, else drugi neki composable

        when(index){
            0-> WorkoutSelector(WorkoutType.GYM,viewModel, navigateToOngoing)
            1-> WorkoutSelector(WorkoutType.CARDIO,viewModel,navigateToOngoing)
        }

    }
}

fun getPageCount(workoutState: WorkoutState): Int =
    when(workoutState){
        WorkoutState.ONGOING->1
        WorkoutState.PREPARING->2
        WorkoutState.FINISHED->1
    }

fun Color.lighten(factor: Float): Color {
    val newColor = ColorUtils.blendARGB(this.toArgb(), Color.White.toArgb(), factor)
    return Color(newColor)
}
fun Color.darken(factor: Float): Color {
    val newColor = ColorUtils.blendARGB(this.toArgb(), Color.Black.toArgb(), factor)
    return Color(newColor)
}