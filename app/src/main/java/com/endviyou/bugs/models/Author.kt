package com.endviyou.bugs.models

/**
 * Модель данных автора
 * @param name Имя автора
 * @param photoResId ID ресурса с фото (R.drawable.xxx)
 */
data class Author(
    val name: String,
    val photoResId: Int
)