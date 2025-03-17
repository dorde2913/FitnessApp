package com.example.fitnessapp.presentation.stateholders

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.repositories.ExerciseClientRepository
import com.example.fitnessapp.repositories.PassiveMonitoringRepository
import com.example.fitnessapp.repositories.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PassiveViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val passiveMonitoringRepository: PassiveMonitoringRepository,
    @ApplicationContext private val context: Context)
: ViewModel(){

    val steps = passiveMonitoringRepository.steps

    val hrMaxes = passiveMonitoringRepository.hrMaxes
    val calories = passiveMonitoringRepository.calories


    fun subscribe() = viewModelScope.launch {
        passiveMonitoringRepository.subscribe()
    }


    suspend fun clearHRMaxes(hour: Int) =
        passiveMonitoringRepository.resetDailyBPM(hour)



}