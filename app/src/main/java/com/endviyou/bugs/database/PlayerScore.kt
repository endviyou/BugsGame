package com.endviyou.bugs.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_scores")
data class PlayerScore(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nickname: String,
    val score: Int,
    val difficulty: Int,
    val course: String,
    val gender: String,
    val zodiacSign: String,
    val date: Long = System.currentTimeMillis()  // Дата игры
)