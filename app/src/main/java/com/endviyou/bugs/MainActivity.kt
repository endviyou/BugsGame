package com.endviyou.bugs

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerCourse: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Настраиваем Spinner (выпадающий список курсов)
        setupSpinner()
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
}