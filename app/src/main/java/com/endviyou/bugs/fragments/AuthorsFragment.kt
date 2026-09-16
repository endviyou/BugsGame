package com.endviyou.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.endviyou.bugs.R
import com.endviyou.bugs.models.Author

class AuthorsFragment : Fragment() {

    private lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.lvAuthors)

        // Список авторов
        val authors = listOf(
            Author(
                name = "Виктория Патрикеева Индивидуальный Предприниматель-312",
                photoResId = R.drawable.cat
            )
        )

        // Создаём адаптер и применяем
        val adapter = AuthorsAdapter(requireContext(), authors)
        listView.adapter = adapter
    }
}