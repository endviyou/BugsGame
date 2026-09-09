package com.endviyou.bugs

import java.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.endviyou.bugs.models.Player
import com.endviyou.bugs.utils.ZodiacHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale





class MainActivity : AppCompatActivity() {

    private lateinit var spinnerCourse: Spinner
    private lateinit var seekBarDifficulty: SeekBar
    private lateinit var tvDifficultyValue: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var btnSave: Button
    private lateinit var tvResult: TextView
    private lateinit var rgGender: RadioGroup
    private lateinit var ivZodiac: ImageView


    private var selectedDate: Date = Calendar.getInstance().time


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        setupSpinner()
        setupSeekBar()
        setupCalendarView()
        setupSaveButton()
    }

    private fun initViews() {
        spinnerCourse = findViewById(R.id.spinnerCourse)
        seekBarDifficulty = findViewById(R.id.seekBarDifficulty)
        tvDifficultyValue = findViewById(R.id.tvDifficultyValue)
        calendarView = findViewById(R.id.calendarView)
        btnSave = findViewById(R.id.btnSave)
        tvResult = findViewById(R.id.tvResult)
        rgGender = findViewById(R.id.rgGender)
        ivZodiac = findViewById(R.id.ivZodiac)
    }

    private fun setupSpinner() {
        // Находим Spinner по ID
        spinnerCourse = findViewById(R.id.spinnerCourse)

        // Получаем массив курсов из strings.xml
        val courses = resources.getStringArray(R.array.courses)

        // Создаем адаптер для Spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            courses
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Применяем адаптер к Spinner
        spinnerCourse.adapter = adapter
    }

    private fun setupSeekBar() {
        seekBarDifficulty = findViewById(R.id.seekBarDifficulty)
        tvDifficultyValue = findViewById(R.id.tvDifficultyValue)

        // Слушатель изменения ползунка
        seekBarDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Меняем текст в зависимости от значения
                val difficultyText = when (progress) {
                    0, 1 -> "Очень легкий ($progress)"
                    2, 3 -> "Легкий ($progress)"
                    4, 5, 6 -> "Средний ($progress)"
                    7, 8 -> "Сложный ($progress)"
                    9, 10 -> "Очень сложный ($progress)"
                    else -> "Средний ($progress)"
                }
                tvDifficultyValue.text = difficultyText
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Ничего не делаем при начале касания
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Ничего не делаем при окончании касания
            }
        })

        // Устанавливаем начальное значение
        seekBarDifficulty.progress = 5
        tvDifficultyValue.text = "Средний (5)"
    }

    private fun setupCalendarView() {
        calendarView = findViewById(R.id.calendarView)

        val today = Calendar.getInstance()
        val maxDate = today.timeInMillis
        calendarView.maxDate = maxDate

        val defaultDate = Calendar.getInstance()
        defaultDate.set(2000, Calendar.JANUARY, 1)

        calendarView.post {
            calendarView.date = defaultDate.timeInMillis
            selectedDate = defaultDate.time
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val monthDisplay = month + 1

            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time

            val zodiac = ZodiacHelper.getZodiacSign(dayOfMonth, month + 1)

            android.widget.Toast.makeText(
                this,
                "Выбрано: $dayOfMonth.$monthDisplay.$year",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            savePlayerData()
        }
    }

    private fun savePlayerData () {
        val etFullName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName)
        val fullName = etFullName.text.toString().trim()

        if (fullName.isEmpty()) {
            etFullName.error = "Введите ФИО"
            return
        }

        val gender = when (rgGender.checkedRadioButtonId) {
            R.id.rbMale -> "Мужской"
            R.id.rbFemale -> "Женский"
            else -> "Не указан"
        }

        val course = spinnerCourse.selectedItem.toString()

        val birthDate = selectedDate

        val calendar = Calendar.getInstance()
        calendar.time = birthDate
        val zodiac = ZodiacHelper.getZodiacSign(calendar)

        val player = Player(
            fullName = fullName,
            gender = gender,
            course = course,
            difficulty = seekBarDifficulty.progress,
            birthDate = birthDate,
            zodiacSign = zodiac
        )

        displayPlayerInfo(player)
    }

    private fun displayPlayerInfo(player: Player) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val birthDateStr = dateFormat.format(player.birthDate)

        val iconResId = ZodiacHelper.getZodiacIconResID(player.zodiacSign)
        ivZodiac.setImageResource(iconResId)

        val resultText = """
            ИНФОРМАЦИЯ ОБ ИГРОКЕ
            ------------------------
            ФИО: ${player.fullName}
            ПОЛ: ${player.gender}
            КУРС: ${player.course}
            Сложность: ${player.difficulty}/10
            Дата Рождения: $birthDateStr
            ЗЗ: ${player.zodiacSign}
            ------------------------
        """.trimIndent()

        tvResult.text = resultText

        android.widget.Toast.makeText(
            this,
            "Данные сохранены! Привет, ${player.fullName}",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }

}