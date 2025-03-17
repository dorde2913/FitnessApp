package com.example.fitnessapp.presentation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.DAILY_LEN
import com.example.fitnessapp.presentation.dataStore
import com.example.fitnessapp.presentation.stateholders.PassiveViewModel
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun DailyActivityScreen(modifier: Modifier = Modifier, viewModel: PassiveViewModel){

    val context = LocalContext.current



    val steps by viewModel.steps.collectAsState()
    val calories by viewModel.calories.collectAsState()
    //val HRMaxes by viewModel.hrMaxes.collectAsState(initial = listOf())


    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    val length by context.dataStore.data
        .map { preferences ->
            preferences[DAILY_LEN]
        }.collectAsState(initial = 0L)


    /*
    ovde iz viewmodela uzeti sve podatke koji su potrebni
     */


    VerticalPager(
        state = rememberPagerState(pageCount = {1}),
        userScrollEnabled = false,
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text("Daily Activity", color = Color.White)
        DailyActivityChip(label = "Steps $steps", iconResource = R.drawable.stepsicon)
        DailyActivityChip(label = "Time Exercising ${LocalTime.ofNanoOfDay((length?:0) * 1_000_000).format(formatter)}",
           iconResource = R.drawable.stopwatchicon_removebg_preview )
        DailyActivityChip(label = "Calories burned: ${calories}kcal", iconResource = R.drawable.caloriesicon_removebg_preview)

        PhoneChip()
    }

}

@Composable
fun DailyActivityChip(label: String,iconResource: Int){
    Chip(
        onClick = {

        },
        label = {Text(label, color = MaterialTheme.colorScheme.onSurface)},
        modifier = Modifier.padding(vertical = 5.dp).height(30.dp),
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface,
            disabledBackgroundColor = MaterialTheme.colorScheme.surface
        ),
        enabled = false,
        icon = {
            Icon(painterResource(iconResource), null, tint = MaterialTheme.colorScheme.onSurface)
        }
    )
}


@Composable
fun PhoneChip(){
    Chip(
        onClick = {
            //posalji intent na fon
        },
        label = {Text("Open on Phone")},
        modifier = Modifier.height(30.dp)
    )
}