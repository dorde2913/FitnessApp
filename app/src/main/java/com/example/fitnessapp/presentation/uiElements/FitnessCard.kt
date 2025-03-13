package com.example.fitnessapp.presentation.uiElements

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun FitnessCard(modifier: Modifier = Modifier,label: String, onClick: (String)->Unit, image: Int, route: String){
    Chip(
        onClick = {
            onClick(route)
        },
        enabled = true,
        label = {
            Text(text = label)
        },
        icon = {

        },
        colors = ChipDefaults.imageBackgroundChipColors(
            backgroundImagePainter = painterResource(image),
            backgroundImageScrimBrush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colors.surface.copy(alpha = 1f),
                    MaterialTheme.colors.surface.copy(alpha = 0f)
                ),
                start = Offset(100f,100f)
            )
        )
        ,
        modifier = Modifier.padding(2.dp).height(52.dp)

    )
}