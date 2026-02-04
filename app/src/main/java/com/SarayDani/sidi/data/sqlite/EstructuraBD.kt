package com.SarayDani.sidi.data.sqlite

import android.provider.BaseColumns

object EstructuraBD {
    object EntradaRecord : BaseColumns {
        const val NOMBRE_TABLA = "records"
        const val NOMBRE_COLUMNA_PUNTUACION = "puntuacion"
        const val NOMBRE_COLUMNA_FECHA = "fecha"
    }
}