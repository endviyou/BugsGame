package com.endviyou.bugs.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.endviyou.bugs.R
import com.endviyou.bugs.models.Author

class AuthorsAdapter(
    context: Context,
    private val authors: List<Author>
) : ArrayAdapter<Author>(context, 0, authors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_author, parent, false)

        // Устанавливаем розовый фон
        view.setBackgroundColor(android.graphics.Color.parseColor("#ffe4e9"))

        val author = authors[position]
        val ivPhoto = view.findViewById<ImageView>(R.id.ivAuthorPhoto)
        val tvName = view.findViewById<TextView>(R.id.tvAuthorName)

        ivPhoto.setImageResource(author.photoResId)
        tvName.text = author.name

        return view
    }
}  // ← ВОТ ЭТА СКОБКА ЗАКРЫВАЕТ КЛАСС