package com.SarayDani.sidi.data.sqlite

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.SarayDani.sidi.data.GuardarCargarRecord
import com.SarayDani.sidi.model.RecordJuego

/**
 * Implementación de GuardarCargarRecord que utiliza SQLite para almacenar y recuperar el récord del juego.
 */
class ControladorSQLite(context: Context) : GuardarCargarRecord {

    private val dbHelper = FeedReaderDbHelper(context)
    private val TAG = "SQLite"

    /**
     * Consulta la base de datos SQLite para obtener el récord
     */
    override fun recogerRecord(): RecordJuego {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION,
            EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_FECHA
        )

        val sortOrder = "${EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION} DESC" // Orden descendente para obtener el mayor

        val cursor = db.query(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,   // La tabla a consultar
            projection,             // El array de columnas a devolver
            null,                   // Las columnas para la cláusula WHERE
            null,                   // Los valores para la cláusula WHERE
            null,                   // No agrupar las filas
            null,                   // No filtrar por grupos de filas
            sortOrder               // El orden de clasificación
        )

        var record = RecordJuego(0, "")

        with(cursor) {
            if (moveToFirst()) {
                val score = getInt(getColumnIndexOrThrow(EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION))
                val fecha = getString(getColumnIndexOrThrow(EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_FECHA))

                record = RecordJuego(score, fecha)
                Log.d(TAG, "Record leído: $score - $fecha")
            }
        }
        cursor.close()

        return record
    }

    override fun guardarRecord(nuevoRecord: RecordJuego) {
        val db = dbHelper.writableDatabase


        val values = ContentValues().apply {
            put(EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION, nuevoRecord.score)
            put(EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_FECHA, nuevoRecord.fecha)
        }


        val newRowId = db.insert(EstructuraBD.EntradaRecord.NOMBRE_TABLA, null, values)

        Log.d(TAG, "Nueva fila insertada con ID: $newRowId")
    }
}