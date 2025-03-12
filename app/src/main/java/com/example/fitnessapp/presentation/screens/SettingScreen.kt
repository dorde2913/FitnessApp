package com.example.fitnessapp.presentation.screens

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.SelectableChip
import androidx.wear.compose.material.ToggleChip

@Composable
fun SettingScreen(modifier: Modifier = Modifier,context:Context){

    val sharedPref = context.getSharedPreferences("screenAlwaysOn", Context.MODE_PRIVATE)
    var checked by rememberSaveable { mutableStateOf(sharedPref.getBoolean("screenAlwaysOn",false)) }


    ScalingLazyColumn {
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