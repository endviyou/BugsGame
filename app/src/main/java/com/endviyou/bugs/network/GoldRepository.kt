package com.endviyou.bugs.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Репозиторий для получения курса золота
 */
object GoldRepository {

    private var cachedPrice: Double = 0.0
    private var lastUpdate: Long = 0

    suspend fun getGoldPrice(): Double = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        // Кэшируем на 1 час, чтобы не дёргать ЦБ каждую секунду
        if (cachedPrice > 0 && now - lastUpdate < 60 * 60 * 1000) {
            android.util.Log.d("GOLD", "Из кэша: $cachedPrice")
            return@withContext cachedPrice
        }

        try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()

            // 1. Сегодня
            val today = format.format(calendar.time)

            // 2. Неделю назад
            calendar.add(Calendar.DAY_OF_MONTH, -7)
            val weekAgo = format.format(calendar.time)

            android.util.Log.d("GOLD", "Запрос за период: $weekAgo - $today")

            // Запрашиваем данные за неделю
            val response = RetrofitClient.api.getMetals(weekAgo, today)
            android.util.Log.d("GOLD", "Ответ получен, записей: ${response.records.size}")

            // Ищем все записи золота (Code=1)
            val goldRecords = response.records.filter { it.code == "1" }

            // Берем последнюю (самую свежую) запись
            val latestRecord = goldRecords.lastOrNull()

            if (latestRecord != null) {
                cachedPrice = latestRecord.buy.replace(",", ".").toDoubleOrNull() ?: 0.0
                lastUpdate = now
                android.util.Log.d("GOLD", "Найден курс: $cachedPrice на дату ${latestRecord.date}")
            } else {
                android.util.Log.e("GOLD", "Записи о золоте не найдены за период $weekAgo - $today")
            }

            cachedPrice
        } catch (e: Exception) {
            android.util.Log.e("GOLD", "Ошибка запроса: ${e.message}", e)
            cachedPrice
        }
    }

    fun getCachedPrice(): Double = cachedPrice
}