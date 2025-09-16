package com.example.fitnessapp.presentation.screens.WorkoutComposables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardColors
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.stateholders.WorkoutType
import com.example.fitnessapp.presentation.stateholders.WorkoutViewModel

@Composable
fun WorkoutSelector(type: WorkoutType, viewModel: WorkoutViewModel, navigateToOngoing: ()->Unit){
/* Ovaj kod je prilicno uzasan ali dobro */
    val workouts = mapOf(
        Pair(WorkoutType.GYM,viewModel.gymWorkouts.collectAsState(initial = listOf()).value),
        Pair(WorkoutType.CARDIO,viewModel.cardioWorkouts.collectAsState(initial = listOf()).value)
    )

    var index by rememberSaveable { mutableStateOf(0) }
    var rightClicked by rememberSaveable { mutableStateOf(false) }

    val sensorsReady by viewModel.sensorsReady.collectAsState()

    val running = viewModel.exerciseRunning.collectAsState().value
    LaunchedEffect(Unit) {
        //viewModel.finishExercise()
//        if (running) {
//            viewModel.finishExercise()
//        }

        viewModel.prepareExercise()
    }


    if (workouts[type] != null && workouts[type]?.isNotEmpty() == true){
        if (workouts[type]?.size!! >= index) index = 0

        val animatedFrameColor by animateColorAsState(
            targetValue = Color(workouts[type]?.get(index)?.color ?: MaterialTheme.colors.surface.toArgb()) ,
            animationSpec = tween(durationMillis = 500),
            label = "frame color"
        )

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            colors = CardColors(
                containerPainter = ColorPainter(
                    animatedFrameColor
                ),
                contentColor = MaterialTheme.colors.onSurface,
                appNameColor = MaterialTheme.colors.onSurface,
                timeColor = MaterialTheme.colors.onSurface,
                titleColor = MaterialTheme.colors.onSurface,
                subtitleColor = MaterialTheme.colors.onSurface,
            ),
            onClick = {},
            enabled = false
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize()
                    .clickable {
                        //start exercise
                        viewModel.setType(type)

                        viewModel.setLabel(
                            workouts[type]?.get(index)?.label ?: "No label"
                        )
                        if (sensorsReady)
                            navigateToOngoing()

                    }
                    .background(MaterialTheme.colors.surface),
                contentAlignment = Alignment.Center
            ){
                val id = if (type == WorkoutType.GYM) R.drawable.jimjpeg_removebg_preview
                else R.drawable.running1_removebg_preview

                Icon(
                    painterResource(id)
                    , null,
                    tint = MaterialTheme.colors.surface.darken(0.3f),
                    modifier = Modifier.size(
                        if (id == R.drawable.running1_removebg_preview) 150.dp else 400.dp
                    ))

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = if (type ==  WorkoutType.GYM) Alignment.BottomCenter
                    else Alignment.TopCenter
                ){
                    val updown = if(type ==  WorkoutType.GYM) "down" else "up"
                    Text("Swipe $updown",color = MaterialTheme.colors.surface.darken(0.3f), fontSize = 10.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    val leftEnabled = index>0
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(35.dp)
                            .clickable(
                                enabled = leftEnabled
                            ) {
                                index--
                                rightClicked = false
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (leftEnabled)
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                null)
                    }

                    val rightEnabled =  index < (workouts[type]?.size ?: 1)-1

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(35.dp)
                            .clickable(
                                enabled = rightEnabled
                            ) {
                                index++
                                rightClicked = true
                            }
                        ,
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (rightEnabled)
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                null)
                    }
                }

                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ){
                    Text(text = if(sensorsReady)"Tap to Start" else "Preparing sensors..."
                        , color = Color.White, fontSize = 10.sp)


                    AnimatedContent(targetState = index,
                        label = "",
                        transitionSpec = {
                            if (rightClicked){
                                slideInHorizontally(
                                    initialOffsetX = {it}
                                ).togetherWith(slideOutHorizontally(
                                    targetOffsetX = {-it}
                                ))
                            }
                            else{
                                slideInHorizontally(
                                    initialOffsetX = {-it}
                                ).togetherWith(slideOutHorizontally(
                                    targetOffsetX = {it}
                                ))
                            }

                        }
                    ) {i ->

                        Text( text = workouts[type]?.get(i)?.label ?: "No name",
                            color = Color.White, textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                    }


                    Spacer(modifier = Modifier.height(10.dp))

                }
            }


        }
    }

}