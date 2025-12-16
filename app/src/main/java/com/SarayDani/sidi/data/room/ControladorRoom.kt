package com.SarayDani.sidi.data.room

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.SarayDani.sidi.data.GuardarCargarRecord
import com.SarayDani.sidi.model.RecordJuego

class ControladorRoom(context: Context) : GuardarCargarRecord {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "SidiRoom.db"
    ).allowMainThreadQueries().build() // Permitimos consultas en el hilo principal para simplificar

    private val TAG = "ROOM_DB"

    /**
     *
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
     *
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