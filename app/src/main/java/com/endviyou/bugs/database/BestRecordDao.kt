package com.endviyou.bugs.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BestRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BestRecord)

    @Query("SELECT * FROM records WHERE nickname = :nickname AND difficulty = :difficulty LIMIT 1")
    suspend fun getRecord(nickname: String, difficulty: Int): BestRecord?

    @Query("SELECT * FROM records WHERE difficulty = :difficulty ORDER BY score DESC LIMIT 10")
    suspend fun getTopByDifficulty(difficulty: Int): List<BestRecord>

    @Query("SELECT DISTINCT difficulty FROM records ORDER BY difficulty ASC")
    suspend fun getAvailableDifficulties(): List<Int>

    @Query("SELECT * FROM records ORDER BY score DESC LIMIT 10")
    suspend fun getTop10(): List<BestRecord>

    @Query("SELECT * FROM records ORDER BY difficulty ASC, score DESC")
    suspend fun getAllRecords(): List<BestRecord>
}