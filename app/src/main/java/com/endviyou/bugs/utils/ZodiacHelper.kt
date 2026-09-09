package com.endviyou.bugs.utils

import com.endviyou.bugs.R
import java.util.Calendar

object ZodiacHelper {
    fun getZodiacSign(day: Int, month: Int): String {
        return when (month) {
            1 -> if (day >= 21) "Водолей" else "Козерог"
            2 ->  if (day >= 21) "Рыбы" else "Водолей"
            3 -> if (day >= 21) "Овен" else "Рыбы"
            4 -> if (day >= 20) "Телец" else "Овен"
            5 -> if (day >= 21) "Близнецы" else "Телец"
            6 -> if (day >= 21) "Рак" else "Близнецы"
            7 -> if (day >= 23) "Лев" else "Рак"
            8 -> if (day >= 23) "Дева" else "Лев"
            9 -> if (day >= 23) "Весы" else "Дева"
            10 -> if (day >= 24) "Скорпион" else "Весы"
            11 -> if (day >= 22) "Стрелец" else "Скорпион"
            12 -> if (day >= 22) "Козерог" else "Стрелец"
            else -> "КТО ТЫ, ВОИН????"
        }
    }

    fun getZodiacIconResID(zodiacSign: String): Int{
        return when (zodiacSign){
            "Овен" -> R.drawable.oven
            "Телец" -> R.drawable.tel
            "Близнецы" -> R.drawable.bl
            "Рак" -> R.drawable.rak
            "Лев" -> R.drawable.leo
            "Дева" -> R.drawable.deva
            "Весы" -> R.drawable.vesy
            "Скорпион" -> R.drawable.scorpio
            "Стрелец" -> R.drawable.str
            "Козерог" -> R.drawable.kozerog
            "Водолей" -> R.drawable.vodoley
            "Рыбы" -> R.drawable.rybi
            else -> R.drawable.vs
        }
    }

    fun getZodiacSign(calendar: Calendar) : String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // = 0-11
        return getZodiacSign(day, month)
    }

}