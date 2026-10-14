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
public class RecordDao_Impl(
  __db: RoomDatabase,
) : RecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRecord: EntityInsertAdapter<Record>
  init {
    this.__db = __db
    this.__insertAdapterOfRecord = object : EntityInsertAdapter<Record>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `records` (`nickname`,`score`,`difficulty`,`course`,`gender`,`zodiacSign`,`date`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Record) {
        statement.bindText(1, entity.nickname)
        statement.bindLong(2, entity.score.toLong())
        statement.bindLong(3, entity.difficulty.toLong())
        statement.bindText(4, entity.course)
        statement.bindText(5, entity.gender)
        statement.bindText(6, entity.zodiacSign)
        statement.bindLong(7, entity.date)
      }
    }
  }

  public override suspend fun insert(record: Record): Unit = performSuspending(__db, false, true) {
      _connection ->
    __insertAdapterOfRecord.insert(_connection, record)
  }

  public override suspend fun getTopRecords(): List<Record> {
    val _sql: String = "SELECT * FROM records ORDER BY score DESC LIMIT 10"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfNickname: Int = getColumnIndexOrThrow(_stmt, "nickname")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfCourse: Int = getColumnIndexOrThrow(_stmt, "course")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfZodiacSign: Int = getColumnIndexOrThrow(_stmt, "zodiacSign")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _result: MutableList<Record> = mutableListOf()
        while (_stmt.step()) {
          val _item: Record
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
              Record(_tmpNickname,_tmpScore,_tmpDifficulty,_tmpCourse,_tmpGender,_tmpZodiacSign,_tmpDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecord(nickname: String): Record? {
    val _sql: String = "SELECT * FROM records WHERE nickname = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nickname)
        val _columnIndexOfNickname: Int = getColumnIndexOrThrow(_stmt, "nickname")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfCourse: Int = getColumnIndexOrThrow(_stmt, "course")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfZodiacSign: Int = getColumnIndexOrThrow(_stmt, "zodiacSign")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _result: Record?
        if (_stmt.step()) {
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
          _result =
              Record(_tmpNickname,_tmpScore,_tmpDifficulty,_tmpCourse,_tmpGender,_tmpZodiacSign,_tmpDate)
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
