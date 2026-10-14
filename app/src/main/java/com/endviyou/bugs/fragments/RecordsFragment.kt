package com.endviyou.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.endviyou.bugs.R
import com.endviyou.bugs.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecordsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerDifficulty: Spinner
    private lateinit var adapter: RecordsAdapter

    // Текущая выбранная сложность (0 = "Все")
    private var selectedDifficulty = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvRecords)
        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setupSpinner()
        loadRecords(0)
    }

    /**
     * Настройка Spinner со сложностями
     */
    private fun setupSpinner() {
        // Пункты: "Все" + сложности 1-10
        val items = mutableListOf("Все сложности")
        for (i in 1..10) {
            items.add("Сложность $i")
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDifficulty.adapter = adapter

        spinnerDifficulty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // position 0 = "Все", position 1 = "Сложность 1" и т.д.
                selectedDifficulty = position
                loadRecords(selectedDifficulty)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        if (::recyclerView.isInitialized) {
            loadRecords(selectedDifficulty)
        }
    }

    /**
     * Загрузка рекордов с фильтром по сложности
     */
    private fun loadRecords(difficulty: Int) {
        val database = AppDatabase.getInstance(requireContext())
        val bestRecordDao = database.bestRecordDao()

        CoroutineScope(Dispatchers.IO).launch {
            val records = if (difficulty == 0) {
                // Все сложности
                bestRecordDao.getTop10()
            } else {
                // Конкретная сложность
                bestRecordDao.getTopByDifficulty(difficulty)
            }

            withContext(Dispatchers.Main) {
                adapter = RecordsAdapter(records)
                recyclerView.adapter = adapter
            }
        }
    }
}