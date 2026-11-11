package com.endviyou.bugs.fragments

import android.content.Context
import android.os.Bundle
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
import com.endviyou.bugs.database.BestRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.endviyou.bugs.network.GoldRepository
import com.endviyou.bugs.viewmodels.GameViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameFragment : Fragment() {

    private lateinit var gameView: GameView
    private lateinit var tvScore: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnStartStop: Button

    private var roundDuration = 60

    // ViewModel теперь хранит и таймер, и состояние
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

        setupButton()

        // ВАЖНО: Сначала подписываемся, потом запускаем
        setupObservers()

        // Если игра шла до поворота — принудительно запускаем
        if (gameViewModel.isGameRunning.value == true) {
            gameView.startGame()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val price = GoldRepository.getGoldPrice()
            withContext(Dispatchers.Main) {
                gameView.setGoldPrice(price)
            }
        }
    }

    private fun setupObservers() {
        // Таймер
        gameViewModel.timeLeft.observe(viewLifecycleOwner) { seconds ->
            tvTimer.text = "$seconds"
        }

        // Состояние игры
        gameViewModel.isGameRunning.observe(viewLifecycleOwner) { isRunning ->
            if (isRunning) {
                btnStartStop.text = "Стоп"
                // Запускаем жуков, если они еще не бегут
                if (::gameView.isInitialized && !gameView.isRunning) {
                    gameView.startGame()
                }
            } else {
                btnStartStop.text = if (gameViewModel.secondsLeft in 1 until gameViewModel.roundDuration) {
                    "Продолжить"
                } else {
                    "Старт"
                }
                if (::gameView.isInitialized && gameView.isRunning) {
                    gameView.stopGame()
                }
            }
        }

        // Конец игры
        gameViewModel.gameFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                Toast.makeText(requireContext(), "Раунд окончен!", Toast.LENGTH_LONG).show()
                saveScoreToDatabase()
                gameView.setScore(0)
                tvScore.text = "Очки: 0"
                gameViewModel.resetGame()
            }
        }
    }

    private fun setupButton() {
        btnStartStop.setOnClickListener {
            if (gameViewModel.isGameRunning.value == true) {
                gameViewModel.stopGame() // Пауза
            } else {
                // Если время осталось (была пауза), продолжаем. Иначе — новая игра.
                if (gameViewModel.secondsLeft in 1 until gameViewModel.roundDuration) {
                    gameViewModel.continueGame()
                } else {
                    gameViewModel.startNewGame(roundDuration)
                }
            }
        }
    }

    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)
        val speed = prefs.getInt("speed", 5)
        val maxBugs = prefs.getInt("max_bugs", 10)
        roundDuration = prefs.getInt("round_duration", 60)

        gameView.applySettings(speed, maxBugs)
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

}