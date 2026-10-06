package com.endviyou.bugs.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.endviyou.bugs.R
import com.endviyou.bugs.models.Bug
import com.endviyou.bugs.models.BugType
import kotlin.random.Random

/**
 * Кастомная View — игровое поле
 * Рисует жуков, обрабатывает нажатия, показывает всплывающие очки,
 * поддерживает акселерометр (гравитацию) и звук
 */
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), SensorEventListener {

    // ===== ИГРОВЫЕ ОБЪЕКТЫ =====
    private val bugs = mutableListOf<Bug>()
    private val popups = mutableListOf<PopupText>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // ===== ИГРОВОЙ ЦИКЛ =====
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var maxBugs = 10
    private var speedMultiplier = 1f

    // ===== ОЧКИ =====
    var score = 0
        private set
    var onScoreChanged: ((Int) -> Unit)? = null

    // ===== ГРАВИТАЦИЯ (акселерометр) =====
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gravityX = 0f
    private var gravityY = 0f
    private var gravityModeEnabled = false
    private var gravityTimer = 0

    // ===== БОНУС =====
    private var bonusX = 0f
    private var bonusY = 0f
    private val bonusSize = 60f
    private var bonusVisible = false
    private var bonusTimer = 0
    private val bonusIntervalFrames = 15 * 60   // 15 сек × 60 FPS
    private val gravityDurationFrames = 5 * 60  // 5 секунд

    // ===== ЗВУК =====
    private lateinit var soundPool: SoundPool
    private var screamSoundId = 0

    init {
        // Инициализация сенсора
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Инициализация SoundPool
        val audioAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttrs)
            .build()
        screamSoundId = soundPool.load(context, R.raw.bug_scream, 1)
    }

    // ===== SENSOR =====

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            gravityX = -event.values[0]
            gravityY = event.values[1]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sensorManager.unregisterListener(this)
    }

    // ===== POPUP =====

    private data class PopupText(
        val x: Float,
        val y: Float,
        val text: String,
        val color: Int,
        var alpha: Int = 255,
        var offsetY: Float = 0f
    )

    // ===== ИГРОВОЙ ЦИКЛ =====

    private val gameLoop = object : Runnable {
        override fun run() {
            if (isRunning) {
                updateBugs()
                invalidate()
                handler.postDelayed(this, 16)
            }
        }
    }

    private fun updateBugs() {
        val maxX = width.toFloat()
        val maxY = height.toFloat()

        // Обновляем таймер гравитации
        if (gravityModeEnabled) {
            gravityTimer--
            if (gravityTimer <= 0) {
                gravityModeEnabled = false
            }
        }

        // Обновляем таймер бонуса
        bonusTimer++
        if (!bonusVisible && bonusTimer >= bonusIntervalFrames) {
            spawnBonus()
            bonusTimer = 0
        }

        // Двигаем жуков
        bugs.forEach { bug ->
            bug.move(maxX, maxY)

            // Если гравитация включена — добавляем смещение
            if (gravityModeEnabled) {
                bug.x += gravityX * 2f
                bug.y += gravityY * 2f
                bug.x = bug.x.coerceIn(bug.size, maxX - bug.size)
                bug.y = bug.y.coerceIn(bug.size, maxY - bug.size)
            }
        }

        // Обновляем popups
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

    // ===== ОТРИСОВКА =====

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Рисуем жуков
        bugs.forEach { drawBug(canvas, it) }

        // Рисуем бонус
        if (bonusVisible) {
            drawBonus(canvas)
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

        // Индикатор гравитации
        if (gravityModeEnabled) {
            paint.color = Color.argb(100, 255, 215, 0)
            paint.textSize = 40f
            paint.textAlign = Paint.Align.CENTER
            paint.isFakeBoldText = true
            paint.style = Paint.Style.FILL
            canvas.drawText("🌀 ГРАВИТАЦИЯ ${gravityTimer / 60}с", width / 2f, 80f, paint)
        }
    }

    private fun drawBonus(canvas: Canvas) {
        // Золотой круг
        paint.color = Color.rgb(255, 215, 0)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(bonusX, bonusY, bonusSize, paint)

        // Оранжевая обводка
        paint.color = Color.rgb(255, 165, 0)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        canvas.drawCircle(bonusX, bonusY, bonusSize, paint)

        // Звёздочка
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.textSize = bonusSize * 1.2f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("⭐", bonusX, bonusY + bonusSize * 0.4f, paint)
    }

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

        // 2. ЛАПКИ с анимацией
        paint.color = darkColor
        paint.strokeWidth = size * 0.15f
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE

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

        // 3. ТЕЛО
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

        // 8. УСИКИ
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

        paint.style = Paint.Style.FILL
        canvas.drawCircle(
            bug.x - size * 0.5f + antennaWiggle,
            bug.y - size * 1.7f,
            size * 0.1f,
            paint
        )
        canvas.drawCircle(
            bug.x + size * 0.5f - antennaWiggle,
            bug.y - size * 1.7f,
            size * 0.1f,
            paint
        )
    }

    // ===== БОНУС =====

    private fun spawnBonus() {
        if (width == 0 || height == 0) return
        bonusX = Random.nextFloat() * (width - bonusSize * 2) + bonusSize
        bonusY = Random.nextFloat() * (height - bonusSize * 2) + bonusSize
        bonusVisible = true
    }

    private fun activateGravityMode() {
        gravityModeEnabled = true
        gravityTimer = gravityDurationFrames
        bonusVisible = false

        // Играем звук
        soundPool.play(screamSoundId, 1f, 1f, 1, 0, 1f)

        popups.add(
            PopupText(
                x = width / 2f,
                y = height / 2f,
                text = "🌀 ГРАВИТАЦИЯ!",
                color = Color.rgb(255, 215, 0)
            )
        )
    }

    // ===== КАСАНИЯ =====

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y

            // Проверяем бонус ПЕРВЫМ
            if (bonusVisible) {
                val dx = touchX - bonusX
                val dy = touchY - bonusY
                if (Math.sqrt((dx * dx + dy * dy).toDouble()) <= bonusSize) {
                    activateGravityMode()
                    invalidate()
                    return true
                }
            }

            // Ищем жука
            val hitBug = bugs.find { bug ->
                val dx = touchX - bug.x
                val dy = touchY - bug.y
                Math.sqrt((dx * dx + dy * dy).toDouble()) <= bug.size
            }

            if (hitBug != null) {
                bugs.remove(hitBug)
                score += hitBug.points
                onScoreChanged?.invoke(score)
                popups.add(
                    PopupText(
                        x = hitBug.x,
                        y = hitBug.y,
                        text = if (hitBug.points > 0) "+${hitBug.points}" else "${hitBug.points}",
                        color = if (hitBug.points > 0) Color.rgb(0, 200, 0) else Color.RED
                    )
                )
                spawnBug()
            } else {
                score -= 5
                onScoreChanged?.invoke(score)
                popups.add(PopupText(touchX, touchY, "-5", Color.RED))
            }

            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    // ===== УПРАВЛЕНИЕ =====

    fun startGame() {
        if (isRunning) return
        isRunning = true
        bugs.clear()
        popups.clear()
        score = 0
        onScoreChanged?.invoke(score)

        bonusVisible = false
        bonusTimer = 0
        gravityModeEnabled = false

        repeat(maxBugs) { spawnBug() }
        handler.post(gameLoop)
    }

    fun stopGame() {
        isRunning = false
        handler.removeCallbacks(gameLoop)
    }

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

    fun applySettings(speed: Int, maxBugsCount: Int) {
        this.speedMultiplier = speed / 5f
        this.maxBugs = maxBugsCount

        if (isRunning) {
            while (bugs.size > maxBugs) bugs.removeAt(bugs.size - 1)
            while (bugs.size < maxBugs) spawnBug()
        }

        bugs.forEach { bug ->
            val baseSpeed = when (bug.type) {
                BugType.NORMAL -> 3f
                BugType.FAST -> 7f
                BugType.BONUS -> 4f
                BugType.POISON -> 2f
            }
            val newSpeed = baseSpeed * speedMultiplier
            val currentSpeed = Math.sqrt((bug.speedX * bug.speedX + bug.speedY * bug.speedY).toDouble()).toFloat()
            if (currentSpeed > 0) {
                bug.speedX = bug.speedX / currentSpeed * newSpeed
                bug.speedY = bug.speedY / currentSpeed * newSpeed
            }
        }
    }
}