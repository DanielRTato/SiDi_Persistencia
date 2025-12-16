package com.SarayDani.sidi.model

import androidx.room.Entity

/**
 * Utilizo una data class porque solo almacena datos simples
 * Puedes ver los datos guardados en: View - Tool Windows -  Device Explores - data - data - com.SarayDani.sidi
 * @property score Puntuación máxima alcanzada
 * @property fecha Fecha en la que se alcanzó el récord un String con formato (dd/MM/yyyy HH:mm).
 */
@Entity
data class RecordJuego(
    var score: Int = 0, // Puntuación del récord
    var fecha: String = "" // Fecha del record
) {

}