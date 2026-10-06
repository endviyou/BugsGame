package com.endviyou.bugs.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * DAO — интерфейс для работы с таблицей player_scores
 */
@Dao
interface PlayerScoreDao {

    /**
     * Сохранить новую запись
     */
    @Insert
    suspend fun insert(playerScore: PlayerScore)

    /**
     * Получить все записи (для вкладки Рекорды)
     * Сортировка: сначала с большими очками
     */
    @Query("SELECT * FROM player_scores ORDER BY score DESC")
    suspend fun getAllScores(): List<PlayerScore>

    /**
     * Проверить, существует ли игрок с таким ником
     */
    @Query("SELECT COUNT(*) FROM player_scores WHERE nickname = :nickname")
    suspend fun isNicknameExists(nickname: String): Int

    /**
     * Получить уникальных игроков (последняя запись каждого)
     * Для диалога "Выбрать игрока"
     */
    @Query("""
        SELECT * FROM player_scores 
        WHERE id IN (
            SELECT MAX(id) FROM player_scores GROUP BY nickname
        )
        ORDER BY nickname ASC
    """)
    suspend fun getUniquePlayers(): List<PlayerScore>

    /**
     * Получить лучший результат игрока
     */
    @Query("SELECT MAX(score) FROM player_scores WHERE nickname = :nickname")
    suspend fun getBestScore(nickname: String): Int?
}