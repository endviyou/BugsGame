package com.endviyou.bugs.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    var score: Int = 0
        private set

    var roundDuration: Int = 60
    var secondsLeft: Int = 60

    private val _isGameRunning = MutableLiveData(false)
    val isGameRunning: LiveData<Boolean> = _isGameRunning

    private val _timeLeft = MutableLiveData(60)
    val timeLeft: LiveData<Int> = _timeLeft

    private val _gameFinished = MutableLiveData(false)
    val gameFinished: LiveData<Boolean> = _gameFinished

    private var timerJob: Job? = null

    fun updateScore(newScore: Int) {
        score = newScore
    }

    fun startNewGame(duration: Int) {
        score = 0
        roundDuration = duration
        secondsLeft = duration
        _isGameRunning.value = true
        _gameFinished.value = false
        _timeLeft.value = duration
        startTimer()
    }

    fun continueGame() {
        _isGameRunning.value = true
        startTimer()
    }

    fun stopGame() {
        _isGameRunning.value = false
        timerJob?.cancel()
    }

    fun resetGame() {
        stopGame()
        score = 0
        secondsLeft = roundDuration
        _isGameRunning.value = false
        _gameFinished.value = false
        _timeLeft.value = roundDuration
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (secondsLeft > 0) {
                delay(1000L)
                secondsLeft--
                _timeLeft.postValue(secondsLeft)
            }
            _isGameRunning.postValue(false)
            _gameFinished.postValue(true)
        }
    }
}