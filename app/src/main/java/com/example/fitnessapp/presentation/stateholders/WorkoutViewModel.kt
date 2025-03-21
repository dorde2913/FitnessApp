package com.example.fitnessapp.presentation.stateholders

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.presentation.AVG_BPM
import com.example.fitnessapp.presentation.AVG_CAL
import com.example.fitnessapp.presentation.AVG_LEN
import com.example.fitnessapp.presentation.DAILY_LEN
import com.example.fitnessapp.presentation.NUM_WORKOUTS
import com.example.fitnessapp.presentation.dataStore
import com.example.fitnessapp.repositories.ExerciseClientRepository
import com.example.fitnessapp.repositories.WorkoutRepository
import com.example.fitnessapp.services.FitService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Parcelize
data class ExerciseUIState(
    val workoutState: WorkoutState = WorkoutState.PREPARING,
    val type: WorkoutType = WorkoutType.GYM,
    val workoutLabel: String = "",
    val workoutLength: Long = 0L,
    val totalCals: Double = 0.0,
    val averageBPM: Double = 0.0,
    val distance: Double = 0.0,
    val averageSpeed: Double = 0.0
) : Parcelable


enum class WorkoutType{
    GYM,CARDIO
}

enum class WorkoutState{
    PREPARING,ONGOING,FINISHED
}


enum class TimerState{
    RUNNING, PAUSED, RESET
}

@OptIn(kotlinx. coroutines. ExperimentalCoroutinesApi::class)
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository,
    private val exerciseClientRepository: ExerciseClientRepository,
    @ApplicationContext private val context: Context
): ViewModel(){
    private companion object{
        const val KEY = "ExerciseUI"
    }
    private val _uiState = savedStateHandle.getStateFlow(KEY,ExerciseUIState())
    val uiState = _uiState


    val gymWorkouts = workoutRepository.gymWorkouts
    val cardioWorkouts = workoutRepository.cardioWorkouts

    val sensorsReady = exerciseClientRepository.sensorState


    val exerciseRunning = exerciseClientRepository.isOngoing



    fun setOngoing(){
        savedStateHandle[KEY] = _uiState.value.copy(
            workoutState = WorkoutState.ONGOING
        )
    }

    fun setLabel(label: String){
        savedStateHandle[KEY] = _uiState.value.copy(
            workoutLabel = label
        )
    }

    fun setFinished(){
        savedStateHandle[KEY] = _uiState.value.copy(
            workoutState = WorkoutState.FINISHED
        )
    }

    fun resetWorkoutState(){
        savedStateHandle[KEY] = _uiState.value.copy(
            workoutState = WorkoutState.PREPARING
        )
    }

    fun setType(type: WorkoutType){
        savedStateHandle[KEY] = _uiState.value.copy(
            type = type
        )
    }

    fun setCardio(){
        savedStateHandle[KEY] = _uiState.value.copy(
            type = WorkoutType.CARDIO
        )
    }

    fun setGym(){
        savedStateHandle[KEY] = _uiState.value.copy(
            type = WorkoutType.GYM
        )
    }



    fun getWorkoutByLabel(label: String) =
        workoutRepository.getWorkoutByLabel(label)




    /*
    ovde imamo stopericu i uzimanje podataka preko ExerciseClient
     */
    //temp values dok ne odradim repository
    val heartRate = exerciseClientRepository.currentHeartRate
    val totalCals = exerciseClientRepository.totalCals
    val distance = exerciseClientRepository.totalDistance
    val speed = exerciseClientRepository.currentSpeed

    val _averageBPM = exerciseClientRepository.averageBPM

    //stoperica
    private val _elapsedTime = MutableStateFlow(0L)
    private val _timerState = MutableStateFlow(TimerState.RESET)
    val timerState = _timerState.asStateFlow()
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS")

    val stopWatchText = _elapsedTime
        .map {
            LocalTime.ofNanoOfDay(it * 1_000_000).format(formatter)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = "00:00:00:00"
        )

    init{
        _timerState
            .flatMapLatest {
                getTimerFlow(isRunning = it == TimerState.RUNNING)
            }
            .onEach {timeDiff ->
                _elapsedTime.update { it + timeDiff }
                if (_elapsedTime.value >= 1_000)
                    exerciseClientRepository.length = _elapsedTime.value
            }
            .launchIn(CoroutineScope(Dispatchers.Default))
    }

    private fun getTimerFlow(isRunning: Boolean): Flow<Long> {
        return flow{
            var startMilis = System.currentTimeMillis()
            while(isRunning){
                val currentMilis = System.currentTimeMillis()
                val timeDiff = if(currentMilis > startMilis) currentMilis - startMilis
                else 0L
                emit(timeDiff)
                startMilis = System.currentTimeMillis()
                delay(10L)
            }
        }
    }
    fun toggleIsRunning(){
        when(timerState.value){
            TimerState.RUNNING -> _timerState.update{TimerState.PAUSED}
            TimerState.PAUSED,
            TimerState.RESET -> _timerState.update{TimerState.RUNNING}
        }
    }

    fun resetTimer(){
        _timerState.update { TimerState.RESET }
        _elapsedTime.update { 0L }
    }


    fun startExercise() {
        Log.d("EXERCISE START","TYPE: ${uiState.value.type}")
        exerciseClientRepository.currentType = uiState.value.type
        exerciseClientRepository.currentLabel = uiState.value.workoutLabel

        context.startForegroundService(
            Intent().apply {
                setClass(
                    context,
                    FitService::class.java
                )
            }
        )
    }


    fun prepareExercise() =
        exerciseClientRepository.prepareExercise()

    fun finishExercise(){
        //ovde treba uzeti i podatke za overview

        //exerciseClientRepository.endExercise()

        savedStateHandle[KEY] = _uiState.value.copy(
            workoutLength = _elapsedTime.value,
            totalCals = totalCals.value,
            distance = distance.value,
            averageBPM = _averageBPM.value
        )

        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                if (preferences[DAILY_LEN] == null) preferences[DAILY_LEN] = _uiState.value.workoutLength
                else preferences[DAILY_LEN] =
                    _uiState.value.workoutLength + preferences[DAILY_LEN]!!

                if (preferences[NUM_WORKOUTS] == null) preferences[NUM_WORKOUTS] = 1
                else preferences[NUM_WORKOUTS] = preferences[NUM_WORKOUTS]!! + 1

                if (preferences[AVG_LEN] == null) preferences[AVG_LEN] = _uiState.value.workoutLength
                else {
                    preferences[AVG_LEN] =
                        (preferences[AVG_LEN]!! * (preferences[NUM_WORKOUTS]!! - 1)
                        + _uiState.value.workoutLength) / preferences[NUM_WORKOUTS]!!
                }

                if (preferences[AVG_BPM] == null) preferences[AVG_BPM] = _uiState.value.averageBPM.toLong()
                else {
                    preferences[AVG_BPM] =
                        (preferences[AVG_BPM]!! * (preferences[NUM_WORKOUTS]!! - 1)
                                + _uiState.value.averageBPM.toLong()) / preferences[NUM_WORKOUTS]!!
                }

                if (preferences[AVG_CAL] == null) preferences[AVG_CAL] = _uiState.value.totalCals.toLong()
                else {
                    preferences[AVG_CAL] =
                        (preferences[AVG_CAL]!! * (preferences[NUM_WORKOUTS]!! -1)
                            + _uiState.value.totalCals.toLong()) / preferences[NUM_WORKOUTS]!!
                }
            }
        }





        context.stopService(
            Intent().apply {
                setClass(
                    context,
                    FitService::class.java
                )
            }
        )


    }


    fun pauseExercise() =
            exerciseClientRepository.pauseExercise()

    fun resumeExercise() =
        exerciseClientRepository.unpauseExercise()


}

