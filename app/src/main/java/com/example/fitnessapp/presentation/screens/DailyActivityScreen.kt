package com.example.fitnessapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import com.example.fitnessapp.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun DailyActivityScreen(modifier: Modifier = Modifier){
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    /*
    ovde iz viewmodela uzeti sve podatke koji su potrebni
     */


    VerticalPager(
        state = rememberPagerState(pageCount = {1}),
        userScrollEnabled = false,
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text("Daily Activity", color = Color.White)
        DailyActivityChip(label = "Steps 78/1000", iconResource = R.drawable.stepsicon)
        DailyActivityChip(label = "Time Exercising ${LocalTime.ofNanoOfDay(708798 * 1_000_000).format(formatter)}",
           iconResource = R.drawable.stopwatchicon_removebg_preview )
        DailyActivityChip(label = "Calories burned: 798kcal", iconResource = R.drawable.caloriesicon_removebg_preview)

        PhoneChip()
    }

}

@Composable
fun DailyActivityChip(label: String,iconResource: Int){
    Chip(
        onClick = {
            //posalji intent na fon
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