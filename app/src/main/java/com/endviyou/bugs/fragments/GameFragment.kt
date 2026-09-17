package com.endviyou.bugs.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.endviyou.bugs.R
import com.endviyou.bugs.views.GameView

class GameFragment : Fragment() {

    private lateinit var gameView: GameView
    private lateinit var tvScore: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnStartStop: Button

    private var isGameRunning = false
    private var roundDuration = 60
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameView = view.findViewById(R.id.gameView)
        tvScore = view.findViewById(R.id.tvScore)
        tvTimer = view.findViewById(R.id.tvTimer)
        btnStartStop = view.findViewById(R.id.btnStartStop)

        // Загружаем настройки из SharedPreferences
        loadSettings()

        // Слушатель изменения счёта
        gameView.onScoreChanged = { score ->
            tvScore.text = "Очки: $score"
        }

        // Кнопка старт/стоп
        btnStartStop.setOnClickListener {
            if (isGameRunning) {
                stopGame()
            } else {
                startGame()
            }
        }
    }

    /**
     * Загрузка настроек из SharedPreferences
     */
    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)
        val speed = prefs.getInt("speed", 5)
        val maxBugs = prefs.getInt("max_bugs", 10)
        roundDuration = prefs.getInt("round_duration", 60)

        gameView.applySettings(speed, maxBugs)
        tvTimer.text = "⏱ $roundDuration"
    }

    /**
     * Запуск игры + таймер
     */
    private fun startGame() {
        isGameRunning = true
        btnStartStop.text = "Стоп"
        gameView.startGame()

        // Запускаем таймер
        countDownTimer = object : CountDownTimer(roundDuration * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                tvTimer.text = "⏱ $secondsLeft"
            }

            override fun onFinish() {
                stopGame()
                Toast.makeText(
                    requireContext(),
                    "🏁 Раунд окончен! Очки: ${gameView.score}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }.start()
    }

    /**
     * Остановка игры
     */
    private fun stopGame() {
        isGameRunning = false
        btnStartStop.text = "Старт"
        gameView.stopGame()
        countDownTimer?.cancel()
        tvTimer.text = "⏱ $roundDuration"
    }

    override fun onPause() {
        super.onPause()
        if (isGameRunning) {
            stopGame()
        }
    }
}