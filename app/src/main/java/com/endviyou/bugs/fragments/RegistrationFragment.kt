package com.endviyou.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.endviyou.bugs.R
import com.endviyou.bugs.models.Player
import com.endviyou.bugs.utils.ZodiacHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegistrationFragment : Fragment() {

    // Объявляем все элементы интерфейса
    private lateinit var spinnerCourse: Spinner
    private lateinit var seekBarDifficulty: SeekBar
    private lateinit var tvDifficultyValue: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var btnSave: Button
    private lateinit var tvResult: TextView
    private lateinit var rgGender: RadioGroup
    private lateinit var ivZodiac: ImageView

    private var selectedDate: Date = Calendar.getInstance().time

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupSpinner()
        setupSeekBar()
        setupCalendarView()
        setupSaveButton()
    }

    private fun initViews(view: View) {
        spinnerCourse = view.findViewById(R.id.spinnerCourse)
        seekBarDifficulty = view.findViewById(R.id.seekBarDifficulty)
        tvDifficultyValue = view.findViewById(R.id.tvDifficultyValue)
        calendarView = view.findViewById(R.id.calendarView)
        btnSave = view.findViewById(R.id.btnSave)
        tvResult = view.findViewById(R.id.tvResult)
        rgGender = view.findViewById(R.id.rgGender)
        ivZodiac = view.findViewById(R.id.ivZodiac)
    }

    private fun setupSpinner() {
        val courses = resources.getStringArray(R.array.courses)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            courses
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourse.adapter = adapter
    }

    private fun setupSeekBar() {
        seekBarDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
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

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarDifficulty.progress = 5
        tvDifficultyValue.text = "Средний (5)"
    }

    private fun setupCalendarView() {
        val today = Calendar.getInstance()
        calendarView.maxDate = today.timeInMillis

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
                requireContext(),
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

    private fun savePlayerData() {
        val etFullName = view?.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName)
        val fullName = etFullName?.text.toString().trim()

        if (fullName.isEmpty()) {
            etFullName?.error = "Введите ФИО"
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
            requireContext(),
            "Данные сохранены! Привет, ${player.fullName}",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }
}