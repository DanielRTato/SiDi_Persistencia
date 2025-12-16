package com.SarayDani.sidi.controller.room

import androidx.room.Database
import androidx.room.RoomDatabase

// Aquí indicamos que la tabla se crea a partir de EntidadRecord
@Database(entities = [EntidadRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}