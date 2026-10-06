package com.endviyou.bugs.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица игроков и их очков
 */
@Entity(tableName = "player_scores")
data class PlayerScore(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nickname: String,        // Имя игрока (уникальное)
    val score: Int,              // Очки
    val difficulty: Int,         // Уровень сложности (1-10)
    val course: String,          // Курс
    val gender: String,          // Пол
    val zodiacSign: String,      // Знак зодиака
    val date: Long = System.currentTimeMillis()  // Дата игры
)