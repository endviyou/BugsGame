package com.endviyou.bugs.models

import java.util.Date

data class Player(
    val fullName: String,
    val gender: String,
    val course: String,
    val difficulty: Int,
    val birthDate: Date,
    val zodiacSign: String
)