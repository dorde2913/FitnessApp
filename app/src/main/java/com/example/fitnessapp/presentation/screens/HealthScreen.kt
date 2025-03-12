package com.example.fitnessapp.presentation.screens

import android.provider.ContactsContract.Data
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.unregisterMeasureCallback
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.ScalingParams
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import com.example.fitnessapp.presentation.theme.FitnessAppTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.common.component.TextComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

@Composable
fun HealthScreen(modifier: Modifier = Modifier){

    var currentHR by rememberSaveable { mutableStateOf(0.0) }


    val healthClient = HealthServices.getClient(LocalContext.current)
    val measureClient = healthClient.measureClient

    val exerciseClient = healthClient.exerciseClient

    val passiveMonitoringClient = healthClient.passiveMonitoringClient

    val heartRateCallback = object : MeasureCallback{
        override fun onDataReceived(data: DataPointContainer) {
            if (data.getData(DataType.HEART_RATE_BPM).isNotEmpty()){
                currentHR = data.getData(DataType.HEART_RATE_BPM)[0].value
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

    VerticalPager(
        state = rememberPagerState(pageCount = {2})
    ) {
        if (it == 0) HeartRateTopScreen(heartRate = currentHR)
        else DailyRanges()
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
fun DailyMinMaxHR(){
    Row(
        modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column(
            modifier = Modifier.weight(1f)
        ){
            Text("Daily Min", color = Color.White)
            Text("min", color = Color.White)
        }
        VerticalDivider(color = Color.White, modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ){
            Text("Daily Max", color = Color.White, textAlign = TextAlign.Right)
            Text("max", color = Color.White, textAlign = TextAlign.Right)
        }
    }
}


@Composable
fun BPMRange(label: String, min: Double, max: Double){
    Text("$label BPM range: $min -- $max", color = Color.White)
}

@Composable
fun HeartRateTopScreen(modifier: Modifier = Modifier,heartRate: Double){
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CurrentHeartRate(heartRate)
        Spacer(modifier = Modifier.height(10.dp))
        DailyMinMaxHR()
    }
}

@Composable
fun DailyRanges(){
    //graph
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(5, 6, 5, 2, 11, 8, 5, 2, 15, 11, 8, 13, 12, 10, 2, 7) }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(50.dp))
        CartesianChartHost(
            rememberCartesianChart(
                rememberColumnCartesianLayer(

                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
            modelProducer,
            modifier = Modifier.padding(horizontal = 10.dp).size(width = 200.dp, height = 70.dp)
        )
        //Spacer(modifier = Modifier.height(70.dp).background(Color.Blue))
        Text("Resting range: 70-80bpm", color = Color.White)
    }

}