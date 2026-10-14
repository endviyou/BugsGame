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
import com.endviyou.bugs.network.GoldRepository
import com.endviyou.bugs.viewmodels.GameViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.endviyou.bugs.database.BestRecord

class GameFragment : Fragment() {

    private lateinit var gameView: GameView
    private lateinit var tvScore: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnStartStop: Button

    private var roundDuration = 60
    private var countDownTimer: CountDownTimer? = null

    private val gameViewModel: GameViewModel by viewModel()

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

        loadSettings()

        // Восстанавливаем счёт
        gameView.setScore(gameViewModel.score)
        tvScore.text = "Очки: ${gameViewModel.score}"

        gameView.onScoreChanged = { score ->
            gameViewModel.updateScore(score)
            tvScore.text = "Очки: $score"
        }

        // ↓↓↓ ЛОГИКА КНОПКИ ↓↓↓
        btnStartStop.setOnClickListener {
            if (gameViewModel.isGameRunning) {
                // Пауза
                stopGame()
            } else {
                // Игра шла до поворота и есть оставшееся время? Продолжаем.
                if (gameViewModel.secondsLeft in 1 until gameViewModel.roundDuration) {
                    continueGame()
                } else {
                    // Новая игра
                    startGame()
                }
            }
        }

        // ВОССТАНОВЛЕНИЕ ПОСЛЕ ПОВОРОТА
        if (gameViewModel.isGameRunning) {
            gameViewModel.stopGame()
        }
        // Показываем текущее время
        tvTimer.text = "${gameViewModel.secondsLeft}"
        btnStartStop.text = if (gameViewModel.secondsLeft in 1 until gameViewModel.roundDuration) {
            "Продолжить"
        } else {
            "Старт"
        }
        // ↑↑↑ КОНЕЦ ↑↑↑

        CoroutineScope(Dispatchers.IO).launch {
            val price = GoldRepository.getGoldPrice()
            withContext(Dispatchers.Main) {
                gameView.setGoldPrice(price)
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
    }

    private fun startGame() {
        loadSettings()

        gameViewModel.startNewGame(roundDuration)

        gameView.setScore(0)
        tvScore.text = "Очки: 0"
        tvTimer.text = "$roundDuration"

        btnStartStop.text = "Стоп"
        gameView.startGame()
        startCountDownTimer(roundDuration * 1000L)
    }

    private fun continueGame() {
        gameViewModel.continueGame()
        btnStartStop.text = "Стоп"
        gameView.startGame()  // запускаем жуков заново
        // Таймер продолжает с оставшегося времени
        startCountDownTimer(gameViewModel.secondsLeft * 1000L)
    }

    private fun startCountDownTimer(millisInFuture: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(millisInFuture, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                gameViewModel.updateTime(secondsLeft)
                tvTimer.text = "$secondsLeft"
            }

            override fun onFinish() {
                stopGame()
                Toast.makeText(
                    requireContext(),
                    "Раунд окончен! Очки: ${gameViewModel.score}",
                    Toast.LENGTH_LONG
                ).show()
                saveScoreToDatabase()
            }
        }.start()
    }

    private fun stopGame() {
        gameViewModel.stopGame()
        btnStartStop.text = "Продолжить"
        gameView.stopGame()
        countDownTimer?.cancel()
        // НЕ сбрасываем время
    }

    private fun saveScoreToDatabase() {
        val nickname = getCurrentNickname() ?: return
        val score = gameViewModel.score

        val prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)
        val difficulty = prefs.getInt("speed", 5)

        val userPrefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val course = userPrefs.getString("course", "Не указан") ?: "Не указан"
        val gender = userPrefs.getString("gender", "Не указан") ?: "Не указан"
        val zodiac = userPrefs.getString("zodiac", "Не указан") ?: "Не указан"

        val database = AppDatabase.getInstance(requireContext())
        val playerScoreDao = database.playerScoreDao()
        val bestRecordDao = database.bestRecordDao()

        CoroutineScope(Dispatchers.IO).launch {
            // 1. Всегда сохраняем в историю
            playerScoreDao.insert(
                PlayerScore(
                    nickname = nickname,
                    score = score,
                    difficulty = difficulty,
                    course = course,
                    gender = gender,
                    zodiacSign = zodiac
                )
            )

            // 2. Логика рекорда: обновить, если лучше
            val existing = bestRecordDao.getRecord(nickname, difficulty)
            if (existing == null || score > existing.score) {
                bestRecordDao.insert(
                    BestRecord(
                        nickname = nickname,
                        difficulty = difficulty,
                        score = score,
                        course = course,
                        gender = gender,
                        zodiacSign = zodiac
                    )
                )
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Результат сохранён!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getCurrentNickname(): String? {
        val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return prefs.getString("nickname", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}