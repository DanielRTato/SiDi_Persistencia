package com.SarayDani.sidi.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

// Aquí indicamos que la tabla se crea a partir de EntidadRecord
@Database(entities = [EntidadRecord::class], version = 1) // Si cambias el esquema de la BD, debes incrementar la versión
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao // Es el método que da aceso a los métodos de la DAO
}