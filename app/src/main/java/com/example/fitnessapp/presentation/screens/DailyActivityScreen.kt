package com.example.fitnessapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import com.example.fitnessapp.R


@Composable
fun DailyActivityScreen(modifier: Modifier = Modifier){

    VerticalPager(
        state = rememberPagerState(pageCount = {1}),
        userScrollEnabled = false
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text("Daily Activity", color = Color.White)
        StepsCounter()
        WorkoutTime()
        CaloriesCounter()
        PhoneChip()
    }

}

@Composable
fun StepsCounter(){
    Row(

    ){
        //ovde ce i ikonica neka da bude
        Text("Steps: 80/1000", color = Color.White)
    }
}

@Composable
fun WorkoutTime(){
    Row(){
        Text("Time Exercising: 1:47", color =  Color.White)
    }

}

@Composable
fun CaloriesCounter(){
    Row(){
        Text("Calories burned: 876", color =  Color.White)
    }

}

@Composable
fun PhoneChip(){
    Chip(
        onClick = {},
        label = {Text("Open on Phone")}
    )
}