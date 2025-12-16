package com.SarayDani.sidi.controller.room

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.SarayDani.sidi.controller.GuardarCargarRecord
import com.SarayDani.sidi.model.RecordJuego

class ControladorRoom(context: Context) : GuardarCargarRecord {

    // Inicializamos Room
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "SidiRoom.db"
    ).allowMainThreadQueries().build()

    private val TAG = "ROOM_DB"

    /**
     * TRADUCCIÓN: De Entidad (BD) a Modelo (Juego)
     */
    override fun recogerRecord(): RecordJuego {
        val entidad: EntidadRecord? = db.recordDao().getRecordMaximo()

        return if (entidad != null) {
            Log.d(TAG, "Record recuperado: ${entidad.puntuacion}")
            // Mapeamos los datos
            RecordJuego(entidad.puntuacion, entidad.fecha)
        } else {
            Log.d(TAG, "No hay datos, devolviendo vacío")
            RecordJuego(0, "")
        }
    }

    /**
     * TRADUCCIÓN: De Modelo (Juego) a Entidad (BD)
     */
    override fun guardarRecord(nuevoRecord: RecordJuego) {
        // Creamos la entidad a partir del objeto del juego
        val nuevaEntidad = EntidadRecord(
            puntuacion = nuevoRecord.score,
            fecha = nuevoRecord.fecha
        )

        // Guardamos la entidad
        db.recordDao().insert(nuevaEntidad)
        Log.d(TAG, "Record convertido y guardado en Room: ${nuevoRecord.score}")
    }
}