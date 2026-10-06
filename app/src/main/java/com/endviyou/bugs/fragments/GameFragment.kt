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

import com.endviyou.bugs.database.AppDatabase
import com.endviyou.bugs.database.PlayerScore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // Первая загрузка настроек
        loadSettings()

        gameView.onScoreChanged = { score ->
            tvScore.text = "Очки: $score"
        }

        btnStartStop.setOnClickListener {
            if (isGameRunning) {
                stopGame()
            } else {
                startGame()
            }
        }
    }

    /**
     * ВАЖНО: Вызывается каждый раз, когда вкладка становится видимой
     * Здесь перечитываем настройки!
     */
    override fun onResume() {
        super.onResume()
        // Перечитываем настройки каждый раз при показе вкладки
        if (::gameView.isInitialized) {
            loadSettings()
        }
    }

    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)
        val speed = prefs.getInt("speed", 5)
        val maxBugs = prefs.getInt("max_bugs", 10)
        roundDuration = prefs.getInt("round_duration", 60)

        gameView.applySettings(speed, maxBugs)
        tvTimer.text = "$roundDuration"
    }

    private fun saveScoreToDatabase() {
        val nickname = getCurrentNickname() ?: return
        val score = gameView.score

        // Читаем данные из SharedPreferences
        val prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)
        val difficulty = prefs.getInt("speed", 5)
        val course = "4 курс"  // ← берем из регистрации
        val gender = "Женский"
        val zodiac = "Скорпион"

        // Получаем базу
        val database = AppDatabase.getInstance(requireContext())
        val dao = database.playerScoreDao()

        // Сохраняем асинхронно
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(
                PlayerScore(
                    nickname = nickname,
                    score = score,
                    difficulty = difficulty,
                    course = course,
                    gender = gender,
                    zodiacSign = zodiac
                )
            )

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Результат сохранён!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getCurrentNickname(): String? {
        val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return prefs.getString("nickname", null)
    }

    private fun startGame() {
        isGameRunning = true
        btnStartStop.text = "Стоп"
        gameView.startGame()

        countDownTimer = object : CountDownTimer(roundDuration * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                tvTimer.text = "$secondsLeft"
            }

            override fun onFinish() {
                stopGame()
                Toast.makeText(
                    requireContext(),
                    "Раунд окончен! Очки: ${gameView.score}",
                    Toast.LENGTH_LONG
                ).show()
                saveScoreToDatabase()
            }
        }.start()
    }

    private fun stopGame() {
        isGameRunning = false
        btnStartStop.text = "Старт"
        gameView.stopGame()
        countDownTimer?.cancel()
        tvTimer.text = "$roundDuration"
    }

    override fun onPause() {
        super.onPause()
        if (isGameRunning) {
            stopGame()
        }
    }
}