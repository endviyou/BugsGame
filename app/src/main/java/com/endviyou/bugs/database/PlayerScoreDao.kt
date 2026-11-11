package com.endviyou.bugs.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlayerScoreDao {

    /**
     * Сохранить новую запись
     */
    @Insert
    suspend fun insert(playerScore: PlayerScore)

    /**
     * Получить все записи
     */
    @Query("""
    SELECT * FROM player_scores 
    WHERE id IN (
        SELECT id FROM player_scores 
        WHERE (nickname, score) IN (
            SELECT nickname, MAX(score) 
            FROM player_scores 
            GROUP BY nickname
        )
    )
    ORDER BY score DESC
""")
    suspend fun getAllScores(): List<PlayerScore>

    @Query("SELECT COUNT(*) FROM player_scores WHERE nickname = :nickname")
    suspend fun isNicknameExists(nickname: String): Int



    @Query("""
        SELECT * FROM player_scores 
        WHERE id IN (
            SELECT MAX(id) FROM player_scores GROUP BY nickname
        )
        ORDER BY nickname ASC
    """)
    suspend fun getUniquePlayers(): List<PlayerScore>

    @Query("SELECT MAX(score) FROM player_scores WHERE nickname = :nickname")
    suspend fun getBestScore(nickname: String): Int?
}