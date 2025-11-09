package com.SarayDani.sidi

import androidx.compose.ui.graphics.Color

data class Datos (
    var ronda: Int = 0,                       // Número de ronda actual
    var secuencia: MutableList<Int> = mutableListOf(),  // Secuencia generada por el juego
    var secuenciaIntroducida: MutableList<Int> = mutableListOf(), // Secuencia del jugador
    var record: Int = 0,                      // Máximo número de rondas superadas
    var estados: Estados = Estados.Inicio,    // Estado actual del juego
)

enum class Colores(val color: Color, val txt: String) {
    CLASE_ROJO(color = Color.Red, txt = "rojo"),
    CLASE_VERDE(color = Color.Green, txt = "verde"),
    CLASE_AZUL(color = Color.Blue, txt = "azul"),
    CLASE_AMARILLO(color = Color.Yellow, txt = "amarillo"),
    CLASE_START(color = Color.Magenta, txt = "Start")
}

enum class Estados {
    Inicio,
    GenerarSecuencia,
    IntroducirSecuencia,
    GameOver,
    Pausa
}