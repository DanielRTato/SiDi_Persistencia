package com.SarayDani.sidi.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_records")
data class EntidadRecord(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "puntuacion") val puntuacion: Int,
    @ColumnInfo(name = "fecha") val fecha: String
)