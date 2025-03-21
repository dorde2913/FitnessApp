package com.example.fitnessapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.unregisterMeasureCallback
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.MAX_KEY
import com.example.fitnessapp.presentation.MIN_KEY
import com.example.fitnessapp.presentation.dataStore
import com.example.fitnessapp.presentation.screens.WorkoutComposables.lighten
import com.example.fitnessapp.presentation.stateholders.PassiveViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun HealthScreen(modifier: Modifier = Modifier, viewModel: PassiveViewModel){

    var currentHR by rememberSaveable { mutableStateOf(0.0) }


    val healthClient = HealthServices.getClient(LocalContext.current)
    val measureClient = healthClient.measureClient


    val context = LocalContext.current
    val min by context.dataStore.data
        .map{
            it[MIN_KEY]
        }.collectAsState(initial = 0)

    val max by context.dataStore.data
        .map{
            it[MAX_KEY]
        }.collectAsState(initial = 0)



    val heartRateCallback = object : MeasureCallback{
        override fun onDataReceived(data: DataPointContainer) {
            if (data.getData(DataType.HEART_RATE_BPM).isNotEmpty()){
                currentHR = data.getData(DataType.HEART_RATE_BPM)[0].value


                if (max == null) CoroutineScope(Dispatchers.Default).launch {
                    context.dataStore.edit { preferences ->
                        preferences[MAX_KEY] = currentHR.toInt()
                    }
                }


                if (min == null) CoroutineScope(Dispatchers.Default).launch {
                    context.dataStore.edit { preferences ->
                        preferences[MIN_KEY] = 0
                    }
                }


                if ((currentHR < (min ?: 0) && currentHR.toInt() != 0)|| min == 0){
                    CoroutineScope(Dispatchers.Default).launch {
                        context.dataStore.edit { preferences ->
                            preferences[MIN_KEY] = currentHR.toInt()
                        }
                    }
                }


                if (currentHR > (max ?: 0)){
                    CoroutineScope(Dispatchers.Default).launch {
                        context.dataStore.edit { preferences ->
                            preferences[MAX_KEY] = currentHR.toInt()
                        }
                    }
                }
            }


        }

        override fun onAvailabilityChanged(
            dataType: DeltaDataType<*, *>,
            availability: Availability
        ) {
            println("availability")
        }
    }


    measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM,heartRateCallback)

    DisposableEffect(Unit) {
        onDispose {
            CoroutineScope(Dispatchers.Main).launch {
                measureClient.unregisterMeasureCallback(DataType.HEART_RATE_BPM,heartRateCallback)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            Icon(
                painter = painterResource(R.drawable.heartrateicon_removebg_preview),
                contentDescription = null,
                tint = Color.Black.lighten(0.2f)
            )
        }
        VerticalPager(
            state = rememberPagerState(pageCount = {2})
        ) {

                if (it == 0) HeartRateTopScreen(heartRate = currentHR,min = min?:0, max = max?:0)
                else DailyRanges(viewModel = viewModel)
        }

    }

}
@Composable
fun CurrentHeartRate(heartRate: Double){
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Current Heart Rate", color = Color.White)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = "$heartRate", fontSize = 40.sp, color = Color.White)
        Text(text = "bpm",color = Color.White, fontSize = 10.sp)

        //modifier.offset
    }
}

@Composable
fun DailyMinMaxHR(min: Int, max: Int){


    Row(
        modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column(
            modifier = Modifier.weight(1f)
        ){
            Text("Daily Min", color = Color.White)
            Text(min.toString(), color = Color.White)
        }
        VerticalDivider(color = Color.White, modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ){
            Text("Daily Max", color = Color.White, textAlign = TextAlign.Right)
            Text(max.toString(), color = Color.White, textAlign = TextAlign.Right)
        }
    }
}


@Composable
fun BPMRange(label: String, min: Double, max: Double){
    Text("$label BPM range: $min -- $max", color = Color.White)
}

@Composable
fun HeartRateTopScreen(modifier: Modifier = Modifier,heartRate: Double, min: Int, max: Int){
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CurrentHeartRate(heartRate)
        Spacer(modifier = Modifier.height(10.dp))
        DailyMinMaxHR(min = min, max = max)
    }
}

@Composable
fun DailyRanges(viewModel: PassiveViewModel){
    //graph
    val modelProducer = remember { CartesianChartModelProducer() }
    val HRMaxes by viewModel.hrMaxes.collectAsState(initial = listOf())
    LaunchedEffect(HRMaxes) {
        println(HRMaxes.size)
        if (HRMaxes.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries {
                    series(y = HRMaxes.map { it.value })
                }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(30.dp))
        Text("Daily BPM highs", color = Color.White)
        CartesianChartHost(
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = Fill(color = Color.Red.toArgb()),
                            thickness = 10.dp
                        )
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberAxisLabelComponent(
                        color = Color.White,
                    ),
                    guideline = rememberLineComponent(
                        thickness = 0.dp
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { context, value, verticalAxisPosition ->
                        "${HRMaxes[value.toInt()].hour}h"
                    },
                    label = rememberAxisLabelComponent(
                        color = Color.White,
                    ),
                    guideline = rememberLineComponent(
                        thickness = 0.dp
                    )
                ),

            ),
            zoomState = rememberVicoZoomState(
                initialZoom = Zoom.x(HRMaxes.size.toDouble())
            ),
            modelProducer = modelProducer,
            modifier = Modifier.padding(horizontal = 10.dp).size(width = 200.dp, height = 120.dp),
        )
        //Spacer(modifier = Modifier.height(70.dp).background(Color.Blue))
        //Text("Resting range: 70-80bpm", color = Color.White)
    }

}