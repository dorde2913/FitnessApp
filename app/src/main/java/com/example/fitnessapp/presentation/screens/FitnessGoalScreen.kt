package com.example.fitnessapp.presentation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.AVG_BPM
import com.example.fitnessapp.presentation.AVG_CAL
import com.example.fitnessapp.presentation.AVG_LEN
import com.example.fitnessapp.presentation.DAILY_LEN
import com.example.fitnessapp.presentation.dataStore
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun FitnessGoalScreen(modifier: Modifier = Modifier){
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    /*
    ovde iz viewmodela uzeti sve podatke koji su potrebni
     */
    val context = LocalContext.current

    val length by context.dataStore.data
        .map { preferences ->
            preferences[AVG_LEN]
        }.collectAsState(initial = 0L)

    val calories by context.dataStore.data
        .map { preferences ->
            preferences[AVG_CAL]
        }.collectAsState(initial = 0L)

    val heartRate by context.dataStore.data
        .map { preferences ->
            preferences[AVG_BPM]
        }.collectAsState(initial = 0L)

    if (length!=null)
        VerticalPager(
            state = rememberPagerState(pageCount = {1}),
            userScrollEnabled = false,
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Workout Averages", color = Color.White)
            DailyActivityChip(label = "Workout Length${LocalTime.ofNanoOfDay(length!! * 1_000_000).format(formatter)}"
                , iconResource = R.drawable.stopwatchicon_removebg_preview)
            DailyActivityChip(label = "Calories burned: ${calories}kcal", iconResource = R.drawable.caloriesicon_removebg_preview )
            DailyActivityChip(label = "Heart Rate: ${heartRate}bpm", iconResource = R.drawable.heartrateicon_removebg_preview)

            PhoneChip()
        }
}