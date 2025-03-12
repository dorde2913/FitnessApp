package com.example.fitnessapp.presentation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import com.example.fitnessapp.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun FitnessGoalScreen(modifier: Modifier = Modifier){
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    /*
    ovde iz viewmodela uzeti sve podatke koji su potrebni
     */


    VerticalPager(
        state = rememberPagerState(pageCount = {1}),
        userScrollEnabled = false,
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text("Workout Averages", color = Color.White)
        DailyActivityChip(label = "Workout Length${LocalTime.ofNanoOfDay(708798 * 1_000_000).format(formatter)}"
            , iconResource = R.drawable.stopwatchicon_removebg_preview)
        DailyActivityChip(label = "Calories burned: 77.98kcal", iconResource = R.drawable.caloriesicon_removebg_preview )
        DailyActivityChip(label = "Heart Rate: 102bpm", iconResource = R.drawable.heartrateicon_removebg_preview)

        PhoneChip()
    }
}