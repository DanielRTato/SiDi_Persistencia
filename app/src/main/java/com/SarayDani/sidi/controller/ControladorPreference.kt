package com.SarayDani.sidi.controller

import android.content.Context
import com.SarayDani.sidi.controller.GuardarCargarRecord
import com.SarayDani.sidi.model.RecordJuego

/**
 * Implementación de GuardarCargarRecord usando SharedPreferences.
 * @param context Contexto de la aplicación para acceder a SharedPreferences.
 * https://developer.android.com/reference/android/content/Context
 */
class ControladorPreference(private val context: Context): GuardarCargarRecord {

    private val PREFS_NAME = "preferencias_app" // nombre del archivo xml
    private val KEY_RECORD = "record_score" // clave para el récord
    private val KEY_FECHA = "record_fecha" // clave para la fecha del récord

    /**
     * Recoge el récord guardado en SharedPreferences.
     * @return RecordJuego con el score y la fecha.
     */
    override fun recogerRecord(): RecordJuego {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) // recibe el nombre del archivo y el modo de acceso (private quiere decir que solo esta app puede acceder)

        // Obtenemos los valores, si no existen devuelve 0 y una cadena vacia
        val score = sharedPreferences.getInt(KEY_RECORD, 0)
        val fecha = sharedPreferences.getString(KEY_FECHA, "") ?: ""

        return RecordJuego(score, fecha)
    }

    /**
     * Guarda un nuevo récord en SharedPreferences.
     */
    override fun guardarRecord(nuevoRecord: RecordJuego) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit() // obtener el editor para modificar las preferencias
        editor.putInt(KEY_RECORD, nuevoRecord.score)
        editor.putString(KEY_FECHA, nuevoRecord.fecha)
        editor.apply() // guarda los cambios de forma asíncrona e imediata
        }
    }