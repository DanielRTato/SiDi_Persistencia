package com.SarayDani.sidi.controller.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecordDao {
    @Query("SELECT * FROM tabla_records")
    fun getAll(): List<EntidadRecord>

    @Delete
    fun delete(record: EntidadRecord)

    @Insert
    fun insert(record: EntidadRecord)

    // Devuelve la entidad con mayor puntuación
    @Query("SELECT * FROM tabla_records ORDER BY puntuacion DESC LIMIT 1")
    fun getRecordMaximo(): EntidadRecord?
}