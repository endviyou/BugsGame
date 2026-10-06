package com.endviyou.bugs.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _playerScoreDao: Lazy<PlayerScoreDao> = lazy {
    PlayerScoreDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "da72ca383efb52029761df0ef19e6ecf", "3638f7ceb5ef0c16dfc4bdc0102bda0e") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `player_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nickname` TEXT NOT NULL, `score` INTEGER NOT NULL, `difficulty` INTEGER NOT NULL, `course` TEXT NOT NULL, `gender` TEXT NOT NULL, `zodiacSign` TEXT NOT NULL, `date` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'da72ca383efb52029761df0ef19e6ecf')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `player_scores`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsPlayerScores: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPlayerScores.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayerScores.put("nickname", TableInfo.Column("nickname", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayerScores.put("score", TableInfo.Column("score", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayerScores.put("difficulty", TableInfo.Column("difficulty", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayerScores.put("course", TableInfo.Column("course", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayerScores.put("gender", TableInfo.Column("gender", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayerScores.put("zodiacSign", TableInfo.Column("zodiacSign", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayerScores.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPlayerScores: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPlayerScores: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPlayerScores: TableInfo = TableInfo("player_scores", _columnsPlayerScores,
            _foreignKeysPlayerScores, _indicesPlayerScores)
        val _existingPlayerScores: TableInfo = read(connection, "player_scores")
        if (!_infoPlayerScores.equals(_existingPlayerScores)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |player_scores(com.endviyou.bugs.database.PlayerScore).
              | Expected:
              |""".trimMargin() + _infoPlayerScores + """
              |
              | Found:
              |""".trimMargin() + _existingPlayerScores)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "player_scores")
  }

  public override fun clearAllTables() {
    super.performClear(false, "player_scores")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(PlayerScoreDao::class, PlayerScoreDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun playerScoreDao(): PlayerScoreDao = _playerScoreDao.value
}
