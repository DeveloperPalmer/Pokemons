package com.example.pokemons.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sla.feature.auth.data.storage.AccountDao
import com.sla.feature.auth.data.entity.AccountEntity

@Database(
  entities = [AccountEntity::class],
  version = 1
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun accountDao(): AccountDao
}
