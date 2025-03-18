package com.example.fitnessapp.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fitnessapp.presentation.uiElements.FitnessCard

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onChipClick: (route: String) -> Unit,
               listState: ScalingLazyListState){



    ScalingLazyColumn(
        contentPadding = PaddingValues(
            horizontal = 10.dp,
        ),
        state = listState
    ) {
        item{
            Text("Fitness App", fontSize = 20.sp, modifier = Modifier.padding(vertical = 30.dp))
        }

        for (destination in Destinations){
            if (destination == DestinationHome) continue

            item{
                FitnessCard(label = destination.label, onClick = onChipClick, image = destination.imageID, route = destination.route)
            }
        }

        item{
            Chip(
                colors = ChipDefaults.chipColors(
                    backgroundColor = MaterialTheme.colors.surface,
                )
                ,
                onClick = {
                    onChipClick(DestinationSettings.route)    //settings
                },
                enabled = true,
                label = {
                    Text(DestinationSettings.label)
                },
                modifier = Modifier.padding(vertical = 2.dp).height(52.dp)
            )
        }
    }
}

