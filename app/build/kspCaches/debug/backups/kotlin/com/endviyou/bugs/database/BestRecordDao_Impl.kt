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
public class BestRecordDao_Impl(
  __db: RoomDatabase,
) : BestRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBestRecord: EntityInsertAdapter<BestRecord>
  init {
    this.__db = __db
    this.__insertAdapterOfBestRecord = object : EntityInsertAdapter<BestRecord>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `records` (`id`,`nickname`,`difficulty`,`score`,`course`,`gender`,`zodiacSign`,`date`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BestRecord) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nickname)
        statement.bindLong(3, entity.difficulty.toLong())
        statement.bindLong(4, entity.score.toLong())
        statement.bindText(5, entity.course)
        statement.bindText(6, entity.gender)
        statement.bindText(7, entity.zodiacSign)
        statement.bindLong(8, entity.date)
      }
    }
  }

  public override suspend fun insert(record: BestRecord): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfBestRecord.insert(_connection, record)
  }

  public override suspend fun getRecord(nickname: String, difficulty: Int): BestRecord? {
    val _sql: String = "SELECT * FROM records WHERE nickname = ? AND difficulty = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nickname)
        _argIndex = 2
        _stmt.bindLong(_argIndex, difficulty.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNickname: Int = getColumnIndexOrThrow(_stmt, "nickname")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfCourse: Int = getColumnIndexOrThrow(_stmt, "course")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfZodiacSign: Int = getColumnIndexOrThrow(_stmt, "zodiacSign")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _result: BestRecord?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNickname: String
          _tmpNickname = _stmt.getText(_columnIndexOfNickname)
          val _tmpDifficulty: Int
          _tmpDifficulty = _stmt.getLong(_columnIndexOfDifficulty).toInt()
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpCourse: String
          _tmpCourse = _stmt.getText(_columnIndexOfCourse)
          val _tmpGender: String
          _tmpGender = _stmt.getText(_columnIndexOfGender)
          val _tmpZodiacSign: String
          _tmpZodiacSign = _stmt.getText(_columnIndexOfZodiacSign)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          _result =
              BestRecord(_tmpId,_tmpNickname,_tmpDifficulty,_tmpScore,_tmpCourse,_tmpGender,_tmpZodiacSign,_tmpDate)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTopByDifficulty(difficulty: Int): List<BestRecord> {
    val _sql: String = "SELECT * FROM records WHERE difficulty = ? ORDER BY score DESC LIMIT 10"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, difficulty.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNickname: Int = getColumnIndexOrThrow(_stmt, "nickname")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfCourse: Int = getColumnIndexOrThrow(_stmt, "course")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfZodiacSign: Int = getColumnIndexOrThrow(_stmt, "zodiacSign")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _result: MutableList<BestRecord> = mutableListOf()
        while (_stmt.step()) {
          val _item: BestRecord
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNickname: String
          _tmpNickname = _stmt.getText(_columnIndexOfNickname)
          val _tmpDifficulty: Int
          _tmpDifficulty = _stmt.getLong(_columnIndexOfDifficulty).toInt()
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpCourse: String
          _tmpCourse = _stmt.getText(_columnIndexOfCourse)
          val _tmpGender: String
          _tmpGender = _stmt.getText(_columnIndexOfGender)
          val _tmpZodiacSign: String
          _tmpZodiacSign = _stmt.getText(_columnIndexOfZodiacSign)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          _item =
              BestRecord(_tmpId,_tmpNickname,_tmpDifficulty,_tmpScore,_tmpCourse,_tmpGender,_tmpZodiacSign,_tmpDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAvailableDifficulties(): List<Int> {
    val _sql: String = "SELECT DISTINCT difficulty FROM records ORDER BY difficulty ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<Int> = mutableListOf()
        while (_stmt.step()) {
          val _item: Int
          _item = _stmt.getLong(0).toInt()
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTop10(): List<BestRecord> {
    val _sql: String = "SELECT * FROM records ORDER BY score DESC LIMIT 10"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNickname: Int = getColumnIndexOrThrow(_stmt, "nickname")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfCourse: Int = getColumnIndexOrThrow(_stmt, "course")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfZodiacSign: Int = getColumnIndexOrThrow(_stmt, "zodiacSign")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _result: MutableList<BestRecord> = mutableListOf()
        while (_stmt.step()) {
          val _item: BestRecord
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNickname: String
          _tmpNickname = _stmt.getText(_columnIndexOfNickname)
          val _tmpDifficulty: Int
          _tmpDifficulty = _stmt.getLong(_columnIndexOfDifficulty).toInt()
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpCourse: String
          _tmpCourse = _stmt.getText(_columnIndexOfCourse)
          val _tmpGender: String
          _tmpGender = _stmt.getText(_columnIndexOfGender)
          val _tmpZodiacSign: String
          _tmpZodiacSign = _stmt.getText(_columnIndexOfZodiacSign)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          _item =
              BestRecord(_tmpId,_tmpNickname,_tmpDifficulty,_tmpScore,_tmpCourse,_tmpGender,_tmpZodiacSign,_tmpDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllRecords(): List<BestRecord> {
    val _sql: String = "SELECT * FROM records ORDER BY difficulty ASC, score DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNickname: Int = getColumnIndexOrThrow(_stmt, "nickname")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfCourse: Int = getColumnIndexOrThrow(_stmt, "course")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfZodiacSign: Int = getColumnIndexOrThrow(_stmt, "zodiacSign")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _result: MutableList<BestRecord> = mutableListOf()
        while (_stmt.step()) {
          val _item: BestRecord
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNickname: String
          _tmpNickname = _stmt.getText(_columnIndexOfNickname)
          val _tmpDifficulty: Int
          _tmpDifficulty = _stmt.getLong(_columnIndexOfDifficulty).toInt()
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpCourse: String
          _tmpCourse = _stmt.getText(_columnIndexOfCourse)
          val _tmpGender: String
          _tmpGender = _stmt.getText(_columnIndexOfGender)
          val _tmpZodiacSign: String
          _tmpZodiacSign = _stmt.getText(_columnIndexOfZodiacSign)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          _item =
              BestRecord(_tmpId,_tmpNickname,_tmpDifficulty,_tmpScore,_tmpCourse,_tmpGender,_tmpZodiacSign,_tmpDate)
          _result.add(_item)
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
