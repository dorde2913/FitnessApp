package com.example.fitnessapp.presentation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.AVG_BPM
import com.example.fitnessapp.presentation.AVG_CAL
import com.example.fitnessapp.presentation.AVG_DIST
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

    val distance by context.dataStore.data
        .map { preferences ->
            preferences[AVG_DIST]
        }.collectAsState(initial = 0L)



    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        scalingParams = ScalingLazyColumnDefaults.scalingParams(
            edgeAlpha = 1f,
            edgeScale = 1f
        )
    ) {

        item{
            Text(text = "Workout\nAverages",color = Color.White, fontSize = 25.sp,
                modifier = Modifier.padding(bottom = 20.dp))
        }


        item{

            DailyActivityRow(
                icon = R.drawable.stopwatchicon_removebg_preview,
                iconSize = 25,
                value = LocalTime.ofNanoOfDay((length?:0) * 1_000_000).format(formatter),
                goal = null,
                color = Color.Yellow,
                label = "Duration"
            )

        }

        item{
            DailyActivityRow(
                icon = R.drawable.caloriesicon_removebg_preview,
                iconSize = 25,
                value = "${calories}kcal",
                goal = null,
                color = Color.Magenta,
                label = "Calories burned:"
            )
        }

        item{
            DailyActivityRow(
                icon = R.drawable.heartrateicon_removebg_preview,
                iconSize = 25,
                value = "${heartRate}bpm",
                goal = null,
                color = Color.Red,
                label = "Heart Rate:"
            )
        }

        item{
            DailyActivityRow(
                icon = R.drawable.distanceicon_removebg_preview,
                iconSize = 30,
                value = "${distance?:0}m",
                goal = null,
                color = Color.Cyan,
                label = "Distance\n(Cardio Workouts Only):"
            )
        }

        item{
            Spacer(modifier = Modifier.height(20.dp))
        }
        item{
            PhoneChip("stats")
        }
    }
}

