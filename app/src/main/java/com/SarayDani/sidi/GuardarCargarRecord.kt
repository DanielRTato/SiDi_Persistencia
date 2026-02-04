package com.SarayDani.sidi

import java.util.Date

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


    /**
     * Nuevo métdo para guardar un récord en el top 10.
     * @return true si el record entró en el top 10, false si no
     */
    fun guardarTop10(nuevoRecord: RecordJuego): Boolean
}
