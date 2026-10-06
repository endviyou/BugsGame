package com.endviyou.bugs.utils

import com.endviyou.bugs.database.AppDatabase
import com.endviyou.bugs.database.PlayerScoreDao

/**
 * Проверка ника на валидность
 */
object NicknameValidator {

    /**
     * Проверяет базовую валидность (не пустой, длина)
     */
    fun isBasicValid(nickname: String): ValidationResult {
        val trimmed = nickname.trim()

        return when {
            trimmed.isEmpty() -> ValidationResult.Empty
            trimmed.length < 3 -> ValidationResult.TooShort
            trimmed.length > 20 -> ValidationResult.TooLong
            !trimmed.matches(Regex("[a-zA-Zа-яА-Я0-9_]+")) -> ValidationResult.InvalidChars
            else -> ValidationResult.Valid
        }
    }

    /**
     * Проверяет, не используется ли ник в базе
     */
    suspend fun isNicknameTaken(nickname: String, dao: PlayerScoreDao): Boolean {
        return dao.isNicknameExists(nickname) > 0
    }

    /**
     * Полная проверка
     */
    suspend fun validate(nickname: String, dao: PlayerScoreDao): ValidationResult {
        // Сначала базовая
        val basicResult = isBasicValid(nickname)
        if (basicResult != ValidationResult.Valid) return basicResult

        // Потом проверка на уникальность
        return if (isNicknameTaken(nickname, dao)) {
            ValidationResult.AlreadyTaken
        } else {
            ValidationResult.Valid
        }
    }
}

/**
 * Результат валидации
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    object Empty : ValidationResult()
    object TooShort : ValidationResult()
    object TooLong : ValidationResult()
    object InvalidChars : ValidationResult()
    object AlreadyTaken : ValidationResult()

    fun getMessage(): String = when (this) {
        Valid -> "OK"
        Empty -> "Введите ник"
        TooShort -> "Минимум 3 символа"
        TooLong -> "Максимум 20 символов"
        InvalidChars -> "Только буквы, цифры и _"
        AlreadyTaken -> "Этот ник уже занят"
    }
}