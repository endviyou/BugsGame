package com.endviyou.bugs.viewmodels

import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    var score: Int = 0
        private set

    var isGameRunning: Boolean = false

    var secondsLeft: Int = 60

    var roundDuration: Int = 60

    fun updateScore(newScore: Int) {
        score = newScore
    }

    fun updateTime(seconds: Int) {
        secondsLeft = seconds
    }

    fun startNewGame(duration: Int) {
        score = 0
        secondsLeft = duration
        roundDuration = duration
        isGameRunning = true
    }

    fun continueGame() {
        isGameRunning = true
    }

    fun stopGame() {
        isGameRunning = false
    }
}