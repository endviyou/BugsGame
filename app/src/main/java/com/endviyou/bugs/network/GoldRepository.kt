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

    /**
     * Получить текущий курс золота (руб/грамм)
     */
    suspend fun getGoldPrice(): Double = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (cachedPrice > 0 && now - lastUpdate < 60 * 60 * 1000) {
            android.util.Log.d("GOLD", "Из кэша: $cachedPrice")
            return@withContext cachedPrice
        }

        try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val today = format.format(Calendar.getInstance().time)
            android.util.Log.d("GOLD", "Запрос: date=$today")

            val response = RetrofitClient.api.getMetals(today, today)
            android.util.Log.d("GOLD", "Ответ получен, записей: ${response.records.size}")

            val goldRecord = response.records.find { it.code == "1" }
            android.util.Log.d("GOLD", "Запись золота: $goldRecord")

            if (goldRecord != null) {
                cachedPrice = goldRecord.buy.replace(",", ".").toDoubleOrNull() ?: 0.0
                lastUpdate = now
                android.util.Log.d("GOLD", "Курс: $cachedPrice")
            }

            cachedPrice
        } catch (e: Exception) {
            android.util.Log.e("GOLD", "Ошибка: ${e.message}", e)
            cachedPrice
        }
    }

    /**
     * Получить кэшированную цену (без запроса)
     */
    fun getCachedPrice(): Double = cachedPrice
}