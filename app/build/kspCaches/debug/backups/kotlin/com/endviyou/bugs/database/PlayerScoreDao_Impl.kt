package com.endviyou.bugs.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class PlayerScoreDao_Impl(
  __db: RoomDatabase,
) : PlayerScoreDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPlayerScore: EntityInsertAdapter<PlayerScore>
  init {
    this.__db = __db
    this.__insertAdapterOfPlayerScore = object : EntityInsertAdapter<PlayerScore>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `player_scores` (`id`,`nickname`,`score`,`difficulty`,`course`,`gender`,`zodiacSign`,`date`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PlayerScore) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nickname)
        statement.bindLong(3, entity.score.toLong())
        statement.bindLong(4, entity.difficulty.toLong())
        statement.bindText(5, entity.course)
        statement.bindText(6, entity.gender)
        statement.bindText(7, entity.zodiacSign)
        statement.bindLong(8, entity.date)
      }
    }
  }

  public override suspend fun insert(playerScore: PlayerScore): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfPlayerScore.insert(_connection, playerScore)
  }

  public override suspend fun getAllScores(): List<PlayerScore> {
    val _sql: String = "SELECT * FROM player_scores ORDER BY score DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNickname: Int = getColumnIndexOrThrow(_stmt, "nickname")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfCourse: Int = getColumnIndexOrThrow(_stmt, "course")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfZodiacSign: Int = getColumnIndexOrThrow(_stmt, "zodiacSign")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _result: MutableList<PlayerScore> = mutableListOf()
        while (_stmt.step()) {
          val _item: PlayerScore
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNickname: String
          _tmpNickname = _stmt.getText(_columnIndexOfNickname)
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpDifficulty: Int
          _tmpDifficulty = _stmt.getLong(_columnIndexOfDifficulty).toInt()
          val _tmpCourse: String
          _tmpCourse = _stmt.getText(_columnIndexOfCourse)
          val _tmpGender: String
          _tmpGender = _stmt.getText(_columnIndexOfGender)
          val _tmpZodiacSign: String
          _tmpZodiacSign = _stmt.getText(_columnIndexOfZodiacSign)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          _item =
              PlayerScore(_tmpId,_tmpNickname,_tmpScore,_tmpDifficulty,_tmpCourse,_tmpGender,_tmpZodiacSign,_tmpDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun isNicknameExists(nickname: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM player_scores WHERE nickname = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nickname)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUniquePlayers(): List<PlayerScore> {
    val _sql: String = """
        |
        |        SELECT * FROM player_scores 
        |        WHERE id IN (
        |            SELECT MAX(id) FROM player_scores GROUP BY nickname
        |        )
        |        ORDER BY nickname ASC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNickname: Int = getColumnIndexOrThrow(_stmt, "nickname")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfCourse: Int = getColumnIndexOrThrow(_stmt, "course")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfZodiacSign: Int = getColumnIndexOrThrow(_stmt, "zodiacSign")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _result: MutableList<PlayerScore> = mutableListOf()
        while (_stmt.step()) {
          val _item: PlayerScore
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNickname: String
          _tmpNickname = _stmt.getText(_columnIndexOfNickname)
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpDifficulty: Int
          _tmpDifficulty = _stmt.getLong(_columnIndexOfDifficulty).toInt()
          val _tmpCourse: String
          _tmpCourse = _stmt.getText(_columnIndexOfCourse)
          val _tmpGender: String
          _tmpGender = _stmt.getText(_columnIndexOfGender)
          val _tmpZodiacSign: String
          _tmpZodiacSign = _stmt.getText(_columnIndexOfZodiacSign)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          _item =
              PlayerScore(_tmpId,_tmpNickname,_tmpScore,_tmpDifficulty,_tmpCourse,_tmpGender,_tmpZodiacSign,_tmpDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBestScore(nickname: String): Int? {
    val _sql: String = "SELECT MAX(score) FROM player_scores WHERE nickname = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nickname)
        val _result: Int?
        if (_stmt.step()) {
          val _tmp: Int?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0).toInt()
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
