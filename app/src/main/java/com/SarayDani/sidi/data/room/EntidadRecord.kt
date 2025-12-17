package com.SarayDani.sidi.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Definición de la entidad Record para Room.
 * Representa una tabla en la base de datos con columnas para id, puntuacion y fecha.
 */
@Entity(tableName = "tabla_records")
data class EntidadRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "puntuacion") val puntuacion: Int,
    @ColumnInfo(name = "fecha") val fecha: String
)