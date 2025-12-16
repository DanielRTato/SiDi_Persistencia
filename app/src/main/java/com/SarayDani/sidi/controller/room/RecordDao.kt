package com.SarayDani.sidi.controller.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecordDao {
    @Query("select * from tabla_records")
    fun getAll(): List<RecordDao>

     @Delete
     fun delete(record: EntidadRecord)

     @Insert
     fun insert(record: EntidadRecord)

     @Query("select * from tabla_records order by puntuacion desc limit 1")
     fun getRecordMaximo(): EntidadRecord?
}