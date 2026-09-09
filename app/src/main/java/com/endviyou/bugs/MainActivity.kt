package com.endviyou.bugs

import java.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerCourse: Spinner
    private lateinit var seekBarDifficulty: SeekBar
    private lateinit var tvDifficultyValue: TextView
    private lateinit var calendarView: CalendarView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSpinner()
        setupSeekBar()
        setupCalendarView()
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
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val monthDisplay = month + 1
            android.widget.Toast.makeText(
                this,
                "Выбрано: $dayOfMonth.$monthDisplay.$year",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
}