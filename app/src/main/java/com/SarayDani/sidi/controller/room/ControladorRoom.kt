package com.SarayDani.sidi.controller.room

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.SarayDani.sidi.controller.GuardarCargarRecord
import com.SarayDani.sidi.model.RecordJuego

class ControladorRoom(context: Context) : GuardarCargarRecord {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "SidiRoom.db"
    ).allowMainThreadQueries().build()

    private val TAG = "ROOM_DB"

    /**
     * LEER: Convierte de Base de Datos (Entidad) -> Juego (Modelo)
     */
    override fun recogerRecord(): RecordJuego {
        val entidad: EntidadRecord? = db.recordDao().getRecordMaximo()

        return if (entidad != null) {
            Log.d(TAG, "Record recuperado: ${entidad.puntuacion}")
            // Mapeo manual: Pasamos los datos de la Entidad al RecordJuego
            RecordJuego(entidad.puntuacion, entidad.fecha)
        } else {
            RecordJuego(0, "")
        }
    }

    /**
     * GUARDAR: Convierte de Juego (Modelo) -> Base de Datos (Entidad)
     */
    override fun guardarRecord(nuevoRecord: RecordJuego) {
        // Mapeo manual: Creamos una Entidad nueva con los datos del juego
        val nuevaEntidad = EntidadRecord(
            puntuacion = nuevoRecord.score,
            fecha = nuevoRecord.fecha
        )

        db.recordDao().insert(nuevaEntidad)
        Log.d(TAG, "Guardado en Room. Puntos: ${nuevoRecord.score}")
    }
}