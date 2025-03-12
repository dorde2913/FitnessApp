package com.example.fitnessapp.presentation.screens


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.fitnessapp.R

interface Destination {
    val label: String
    val route: String
    val imageID: Int
}

object DestinationHome : Destination {
    override val label = "Home"
    override val route = "home"
    override val imageID = -1
}

object DestinationWorkout : Destination {
    override val label = "Start new workout"
    override val route = "start_workout"
    override val imageID = R.drawable.jim_background
}

object DestinationDaily : Destination {
    override val label = "Daily Activity"
    override val route = "daily_activity"
    override val imageID = R.drawable.daily_activities
}

object DestinationGoals : Destination {
    override val label = "Exercise Statistics"
    override val route = "fitness_goals"
    override val imageID = R.drawable.fitness_goals2__1_
}

object DestinationHealth : Destination {
    override val label = "Health Metrics"
    override val route = "health_metrics"
    override val imageID = R.drawable.health_stat
}


object DestinationSettings : Destination {
    override val label = "Settings"
    override val route = "settings"
    override val imageID = -1
}

object DestinationWorkoutOngoing : Destination {
    override val label = "Ongoing Workout"
    override val route = "workout_ongoing"
    override val imageID = -1
}

object DestinationWorkoutOverview : Destination {
    override val label = "Workout Overview"
    override val route = "workout_overview"
    override val imageID = -1
}

val Destinations = listOf(DestinationHome,DestinationWorkout, DestinationDaily,DestinationGoals,DestinationHealth)