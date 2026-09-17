package com.endviyou.bugs.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.endviyou.bugs.models.Bug
import com.endviyou.bugs.models.BugType
import kotlin.random.Random

/**
 * Кастомная View — игровое поле
 * Рисует нарисованных жуков, обрабатывает нажатия, показывает всплывающие очки
 */
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Список насекомых
    private val bugs = mutableListOf<Bug>()

    // Список всплывающих текстов
    private val popups = mutableListOf<PopupText>()

    // Paint для рисования
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Игровой цикл
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    // Настройки
    private var maxBugs = 10
    private var speedMultiplier = 1f

    // Очки
    var score = 0
        private set

    // Слушатель изменения счёта
    var onScoreChanged: ((Int) -> Unit)? = null

    /**
     * Класс для всплывающего текста (+10, -5)
     */
    private data class PopupText(
        val x: Float,
        val y: Float,
        val text: String,
        val color: Int,
        var alpha: Int = 255,
        var offsetY: Float = 0f
    )

    /**
     * Игровой цикл — обновляет и перерисовывает
     */
    private val gameLoop = object : Runnable {
        override fun run() {
            if (isRunning) {
                updateBugs()
                invalidate()  // Перерисовать
                handler.postDelayed(this, 16)  // ~60 FPS
            }
        }
    }

    /**
     * Обновление позиций насекомых и всплывающих текстов
     */
    private fun updateBugs() {
        val maxX = width.toFloat()
        val maxY = height.toFloat()

        // Двигаем жуков
        bugs.forEach { it.move(maxX, maxY) }

        // Обновляем popups: уменьшаем alpha, двигаем вверх
        val iterator = popups.iterator()
        while (iterator.hasNext()) {
            val popup = iterator.next()
            popup.alpha -= 15
            popup.offsetY -= 3f
            if (popup.alpha <= 0) {
                iterator.remove()
            }
        }
    }

    /**
     * Отрисовка всех насекомых и всплывающих текстов
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Рисуем каждого жука
        bugs.forEach { bug ->
            drawBug(canvas, bug)
        }

        // Рисуем всплывающие тексты
        popups.forEach { popup ->
            paint.color = popup.color
            paint.alpha = popup.alpha
            paint.textSize = 60f
            paint.textAlign = Paint.Align.CENTER
            paint.isFakeBoldText = true
            paint.style = Paint.Style.FILL
            canvas.drawText(popup.text, popup.x, popup.y + popup.offsetY, paint)
        }
    }

    /**
     * Рисует жука на Canvas
     */
    private fun drawBug(canvas: Canvas, bug: Bug) {
        // Цвета в зависимости от типа
        val bodyColor: Int
        val darkColor: Int
        val accentColor: Int

        when (bug.type) {
            BugType.NORMAL -> {
                bodyColor = Color.rgb(139, 90, 43)
                darkColor = Color.rgb(80, 50, 20)
                accentColor = Color.rgb(60, 35, 15)
            }
            BugType.FAST -> {
                bodyColor = Color.rgb(255, 140, 0)
                darkColor = Color.rgb(180, 90, 0)
                accentColor = Color.rgb(120, 60, 0)
            }
            BugType.BONUS -> {
                bodyColor = Color.rgb(255, 215, 0)
                darkColor = Color.rgb(200, 160, 0)
                accentColor = Color.rgb(150, 120, 0)
            }
            BugType.POISON -> {
                bodyColor = Color.rgb(120, 40, 160)
                darkColor = Color.rgb(75, 0, 130)
                accentColor = Color.rgb(50, 0, 90)
            }
        }

        val size = bug.size

        // 1. ТЕНЬ
        paint.color = Color.argb(60, 0, 0, 0)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(bug.x, bug.y + size * 0.4f, size * 0.9f, paint)

        // 2. ЛАПКИ с анимацией (двигаются вверх-вниз)
        paint.color = darkColor
        paint.strokeWidth = size * 0.15f
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE

        // Анимация: лапки двигаются в зависимости от legPhase
        val legOffset1 = Math.sin(bug.legPhase.toDouble()).toFloat() * size * 0.15f
        val legOffset2 = Math.sin(bug.legPhase.toDouble() + 3.14).toFloat() * size * 0.15f

        // Левая сторона
        canvas.drawLine(
            bug.x - size * 0.5f, bug.y - size * 0.3f,
            bug.x - size * 1.1f, bug.y - size * 0.6f + legOffset1, paint
        )
        canvas.drawLine(
            bug.x - size * 0.6f, bug.y,
            bug.x - size * 1.2f, bug.y + legOffset2, paint
        )
        canvas.drawLine(
            bug.x - size * 0.5f, bug.y + size * 0.3f,
            bug.x - size * 1.1f, bug.y + size * 0.6f + legOffset1, paint
        )

        // Правая сторона
        canvas.drawLine(
            bug.x + size * 0.5f, bug.y - size * 0.3f,
            bug.x + size * 1.1f, bug.y - size * 0.6f + legOffset2, paint
        )
        canvas.drawLine(
            bug.x + size * 0.6f, bug.y,
            bug.x + size * 1.2f, bug.y + legOffset1, paint
        )
        canvas.drawLine(
            bug.x + size * 0.5f, bug.y + size * 0.3f,
            bug.x + size * 1.1f, bug.y + size * 0.6f + legOffset2, paint
        )

        // 3. ТЕЛО ЖУКА (овал)
        paint.style = Paint.Style.FILL
        paint.color = bodyColor
        canvas.drawOval(
            bug.x - size, bug.y - size * 0.8f,
            bug.x + size, bug.y + size * 0.8f,
            paint
        )

        // 4. ЛИНИЯ НА СПИНЕ
        paint.color = darkColor
        paint.strokeWidth = size * 0.08f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(bug.x, bug.y - size * 0.7f, bug.x, bug.y + size * 0.7f, paint)

        // 5. ГОЛОВА
        paint.style = Paint.Style.FILL
        paint.color = darkColor
        canvas.drawCircle(bug.x, bug.y - size * 0.9f, size * 0.4f, paint)

        // 6. ТОЧКИ НА ТЕЛЕ
        paint.color = accentColor
        canvas.drawCircle(bug.x - size * 0.4f, bug.y - size * 0.3f, size * 0.15f, paint)
        canvas.drawCircle(bug.x + size * 0.4f, bug.y - size * 0.3f, size * 0.15f, paint)
        canvas.drawCircle(bug.x - size * 0.4f, bug.y + size * 0.3f, size * 0.15f, paint)
        canvas.drawCircle(bug.x + size * 0.4f, bug.y + size * 0.3f, size * 0.15f, paint)

        // 7. ГЛАЗА
        paint.color = Color.WHITE
        canvas.drawCircle(bug.x - size * 0.15f, bug.y - size * 0.95f, size * 0.1f, paint)
        canvas.drawCircle(bug.x + size * 0.15f, bug.y - size * 0.95f, size * 0.1f, paint)

        paint.color = Color.BLACK
        canvas.drawCircle(bug.x - size * 0.15f, bug.y - size * 0.95f, size * 0.05f, paint)
        canvas.drawCircle(bug.x + size * 0.15f, bug.y - size * 0.95f, size * 0.05f, paint)

        // 8. УСИКИ (анимированные)
        paint.color = darkColor
        paint.strokeWidth = size * 0.08f
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        val antennaWiggle = Math.sin(bug.legPhase.toDouble() * 1.5).toFloat() * size * 0.1f

        canvas.drawLine(
            bug.x - size * 0.2f, bug.y - size * 1.2f,
            bug.x - size * 0.5f + antennaWiggle, bug.y - size * 1.7f, paint
        )
        canvas.drawLine(
            bug.x + size * 0.2f, bug.y - size * 1.2f,
            bug.x + size * 0.5f - antennaWiggle, bug.y - size * 1.7f, paint
        )

        // Кружки на концах усиков
        paint.style = Paint.Style.FILL
        canvas.drawCircle(bug.x - size * 0.5f + antennaWiggle, bug.y - size * 1.7f, size * 0.1f, paint)
        canvas.drawCircle(bug.x + size * 0.5f - antennaWiggle, bug.y - size * 1.7f, size * 0.1f, paint)

        paint.style = Paint.Style.FILL
    }

    /**
     * Обработка нажатий
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y

            // Ищем жука, по которому попали
            val hitBug = bugs.find { bug ->
                val dx = touchX - bug.x
                val dy = touchY - bug.y
                Math.sqrt((dx * dx + dy * dy).toDouble()) <= bug.size
            }

            if (hitBug != null) {
                // Попадание
                bugs.remove(hitBug)
                score += hitBug.points
                onScoreChanged?.invoke(score)

                // Добавляем всплывающий текст
                popups.add(
                    PopupText(
                        x = hitBug.x,
                        y = hitBug.y,
                        text = if (hitBug.points > 0) "+${hitBug.points}" else "${hitBug.points}",
                        color = if (hitBug.points > 0) Color.rgb(0, 200, 0) else Color.RED
                    )
                )

                // Спавним нового жука вместо убитого
                spawnBug()
            } else {
                // Промах — штраф
                score -= 5
                onScoreChanged?.invoke(score)

                popups.add(
                    PopupText(
                        x = touchX,
                        y = touchY,
                        text = "-5",
                        color = Color.RED
                    )
                )
            }

            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    /**
     * Запуск игры
     */
    fun startGame() {
        if (isRunning) return
        isRunning = true
        bugs.clear()
        popups.clear()
        score = 0
        onScoreChanged?.invoke(score)

        // Создаём насекомых
        repeat(maxBugs) { spawnBug() }

        handler.post(gameLoop)
    }

    /**
     * Остановка игры
     */
    fun stopGame() {
        isRunning = false
        handler.removeCallbacks(gameLoop)
    }

    /**
     * Создание нового насекомого
     */
    private fun spawnBug() {
        if (width == 0 || height == 0) return

        val type = BugType.values().random()
        val size = when (type) {
            BugType.NORMAL -> 30f
            BugType.FAST -> 20f
            BugType.BONUS -> 25f
            BugType.POISON -> 35f
        }
        val points = when (type) {
            BugType.NORMAL -> 10
            BugType.FAST -> 25
            BugType.BONUS -> 50
            BugType.POISON -> -20
        }
        val speed = when (type) {
            BugType.NORMAL -> 3f
            BugType.FAST -> 7f
            BugType.BONUS -> 4f
            BugType.POISON -> 2f
        } * speedMultiplier

        bugs.add(
            Bug(
                x = Random.nextFloat() * (width - size * 2) + size,
                y = Random.nextFloat() * (height - size * 2) + size,
                speedX = if (Random.nextBoolean()) speed else -speed,
                speedY = if (Random.nextBoolean()) speed else -speed,
                size = size,
                points = points,
                type = type
            )
        )
    }

    /**
     * Применение настроек
     */
    fun applySettings(speed: Int, maxBugsCount: Int) {
        this.speedMultiplier = speed / 5f
        this.maxBugs = maxBugsCount
    }
}