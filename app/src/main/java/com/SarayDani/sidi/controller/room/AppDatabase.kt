package com.SarayDani.sidi.controller.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.SarayDani.sidi.model.RecordJuego

@Database(entities = [RecordJuego::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao

}