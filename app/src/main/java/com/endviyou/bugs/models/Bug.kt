package com.endviyou.bugs.models

import kotlin.random.Random

/**
 * Модель насекомого с реалистичным движением
 */
data class Bug(
    var x: Float,           // Позиция по X
    var y: Float,           // Позиция по Y
    var speedX: Float,      // Скорость по X
    var speedY: Float,      // Скорость по Y
    val size: Float,        // Размер
    val points: Int,        // Очки за попадание
    val type: BugType,      // Тип насекомого
    var direction: Float = 0f,      // Направление (в градусах)
    var changeDirectionTimer: Int = 0,  // Таймер до смены направления
    var legPhase: Float = 0f        // Фаза движения лапок (для анимации)
) {
    /**
     * Обновление позиции (реалистичное движение)
     */
    fun move(maxX: Float, maxY: Float) {
        // 1. Случайное изменение направления
        changeDirectionTimer--
        if (changeDirectionTimer <= 0) {
            // Меняем направление случайно на ±45 градусов
            val angleChange = Random.nextFloat() * 90f - 45f
            direction += angleChange
            changeDirectionTimer = Random.nextInt(30, 90)  // 0.5-1.5 секунды
        }

        // 2. Небольшое случайное покачивание (wiggle)
        val wiggle = (Random.nextFloat() - 0.5f) * 0.3f
        direction += wiggle

        // 3. Вычисляем скорость по направлению
        val speed = Math.sqrt((speedX * speedX + speedY * speedY).toDouble()).toFloat()
        val radians = Math.toRadians(direction.toDouble())
        speedX = (Math.cos(radians) * speed).toFloat()
        speedY = (Math.sin(radians) * speed).toFloat()

        // 4. Двигаем
        x += speedX
        y += speedY

        // 5. Отталкивание от стенок с изменением направления
        if (x <= size) {
            x = size
            direction = 180f - direction  // Отражаем
            speedX = -speedX
        }
        if (x >= maxX - size) {
            x = maxX - size
            direction = 180f - direction
            speedX = -speedX
        }
        if (y <= size) {
            y = size
            direction = -direction
            speedY = -speedY
        }
        if (y >= maxY - size) {
            y = maxY - size
            direction = -direction
            speedY = -speedY
        }

        // 6. Нормализуем направление (0-360)
        if (direction < 0) direction += 360f
        if (direction >= 360) direction -= 360f

        // 7. Анимация лапок
        legPhase += 0.3f
        if (legPhase > 6.28f) legPhase -= 6.28f
    }
}

/**
 * Типы насекомых
 */
enum class BugType {
    NORMAL,    // Обычный жук — 10 очков
    FAST,      // Быстрый жук — 25 очков
    BONUS,     // Бонусный жук — 50 очков
    POISON     // Ядовитый жук — минус 20 очков
}