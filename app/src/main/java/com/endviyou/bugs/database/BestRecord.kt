package com.endviyou.bugs.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "records",
    indices = [Index(value = ["nickname", "difficulty"], unique = true)]
)
data class BestRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nickname: String,
    val difficulty: Int,
    val score: Int,
    val course: String,
    val gender: String,
    val zodiacSign: String,
    val date: Long = System.currentTimeMillis()
)