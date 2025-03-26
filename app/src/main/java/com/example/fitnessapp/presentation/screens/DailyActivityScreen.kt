package com.example.fitnessapp.presentation.screens


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.ScalingParams
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState

import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.CALS_GOAL
import com.example.fitnessapp.presentation.DAILY_LEN
import com.example.fitnessapp.presentation.STEPS_GOAL
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

    val stepsGoal by context.dataStore.data
        .map { preferences ->
            preferences[STEPS_GOAL]
        }.collectAsState(initial = 0)

    val calsGoal by context.dataStore.data
        .map { preferences ->
            preferences[CALS_GOAL]
        }.collectAsState(initial = 0)



    if (length != null)
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            scalingParams = ScalingLazyColumnDefaults.scalingParams(
                edgeAlpha = 1f,
                edgeScale = 1f
            )
        ) {

            item{
                Text(text = "Daily Activity",color = Color.White, fontSize = 25.sp,
                    modifier = Modifier.padding(bottom = 30.dp))
            }


            item{

                DailyActivityRow(
                    icon = R.drawable.stepsicon,
                    iconSize = 30,
                    value = steps.toString(),
                    goal = stepsGoal.toString(),
                    color = Color.Green,
                    label = "Steps:"
                )


            }
            item{
                 DailyActivityRow(
                     icon = R.drawable.stopwatchicon_removebg_preview,
                     iconSize = 25,
                     value = LocalTime.ofNanoOfDay(length!! * 1_000_000).format(formatter),
                     goal = null,
                     color = Color.Yellow,
                     label = "Time Exercising:"
                 )
            }



            item{
                DailyActivityRow(
                    icon = R.drawable.caloriesicon_removebg_preview,
                    iconSize = 25,
                    value = "$calories",
                    goal = "${calsGoal}kcal",
                    color = Color.Magenta,
                    label = "Calories burned:"
                )
            }

            item{
                Spacer(modifier = Modifier.height(20.dp))
            }
            item{
                PhoneChip("daily")
            }
        }




}


@Composable
fun DailyActivityRow(
    icon: Int,
    value: String,
    goal: String?,
    iconSize: Int,
    color: Color,
    label: String
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){

        Row(
            modifier = Modifier.weight(0.4f,true),
            horizontalArrangement = Arrangement.Center
        ){
            Icon(painterResource(icon),null,tint = color,
                modifier = Modifier.padding(horizontal = 10.dp).size(iconSize.dp)
                    .widthIn(min = 100.dp))
        }
        Column(
            modifier = Modifier.weight(0.6f,true)
        ){
            Text(text = label, color = Color.LightGray, fontSize = 10.sp )
            Row(){
                Text(text = value, color = color, fontSize = 20.sp )
                if (goal != null)
                    Text(text = "/$goal", color = Color.White, fontSize = 20.sp )
            }

        }

    }
}



@Composable
fun PhoneChip(path: String){
    val context = LocalContext.current
    Chip(
        onClick = {
            //posalji intent na
            println("banana")
            launchHandheldApp(context, path)
        },
        label = {Text("Open on Phone")},
        modifier = Modifier.height(50.dp)
    )
}

fun launchHandheldApp(context: Context, path: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        //data = Uri.parse("fitnessapphandheld://open/daily")
        `package` = "com.example.jimapp"
    }

    val remoteHelper = RemoteActivityHelper(context)


    try{
        val result = remoteHelper.startRemoteActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(
                    Uri.parse("fitnessapphandheld://open/$path"))
                .addCategory(Intent.CATEGORY_BROWSABLE))
        println(result)
    }
    catch(e: Exception){
        println("Error")
    }


}