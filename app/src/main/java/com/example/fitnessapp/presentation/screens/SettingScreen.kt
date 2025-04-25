package com.example.fitnessapp.presentation.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.ToggleChip

@Composable
fun SettingScreen(modifier: Modifier = Modifier,context:Context){

    val sharedPref = context.getSharedPreferences("screenAlwaysOn", Context.MODE_PRIVATE)
    var checked by rememberSaveable { mutableStateOf(sharedPref.getBoolean("screenAlwaysOn",false)) }




    ScalingLazyColumn {
        item{
            Text("Settings",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 30.dp).fillMaxWidth())
        }
        item{
            ToggleChip(
                checked = checked,
                label = {
                    Text("Disable sleep mode during workouts", color = Color.White)
                },
                onCheckedChange = { checked = it },
                toggleControl = {
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            with (sharedPref.edit()){
                                putBoolean("screenAlwaysOn",it)
                                apply()
                            }
                        }
                    )
                }
            )
        }


    }
}


@Preview
@Composable
fun SettingsPreview(){
    Scaffold {
        SettingScreen(context = LocalContext.current, modifier = Modifier.padding(it))
    }
}
