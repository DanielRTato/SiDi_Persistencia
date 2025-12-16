package com.SarayDani.sidi.data

import com.SarayDani.sidi.model.RecordJuego

/**
 * Interfaz para guardar y cargar el récord del juego.
 */
interface GuardarCargarRecord {
    /**
     * Recoge el récord guardado.
     * @return El récord del juego como un objeto RecordJuego.
     */
    fun recogerRecord(): RecordJuego

    /**
     * Guarda un nuevo récord para la persistencia.
     */
    fun guardarRecord(nuevoRecord: RecordJuego)
}