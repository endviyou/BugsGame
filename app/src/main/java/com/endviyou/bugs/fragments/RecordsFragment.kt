package com.endviyou.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var adapter: RecordsAdapter

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadRecords()
    }

    override fun onResume() {
        super.onResume()
        // Перезагружаем при каждом показе вкладки
        if (::recyclerView.isInitialized) {
            loadRecords()
        }
    }

    private fun loadRecords() {
        val database = AppDatabase.getInstance(requireContext())
        val dao = database.playerScoreDao()

        CoroutineScope(Dispatchers.IO).launch {
            val scores = dao.getAllScores()

            withContext(Dispatchers.Main) {
                adapter = RecordsAdapter(scores)
                recyclerView.adapter = adapter
            }
        }
    }
}