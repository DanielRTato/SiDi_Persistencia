package com.SarayDani.sidi.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.SarayDani.sidi.model.Estados
import com.SarayDani.sidi.data.GuardarCargarRecord
import com.SarayDani.sidi.data.room.ControladorRoom
import com.SarayDani.sidi.model.RecordJuego
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Ahora hereda de AndroidViewModel en lugar de ViewModel para poder usar aplication context.
 */
class MyViewModel(application: Application) : AndroidViewModel(application) {

    // Tag para los logs
    private val TAG_LOG = "miDebug"

    private val repositorio: GuardarCargarRecord = // Instanciar la implementación concreta
        ControladorRoom(application.applicationContext) //Ahora usamos ControladorRoom

    val estadoActual = MutableStateFlow(Estados.Inicio)
    val secuencia = MutableStateFlow(mutableListOf<Int>())
    val botonEncendido = MutableStateFlow<Int?>(null)
    val secuenciaJugador = MutableStateFlow(mutableListOf<Int>())
    val ronda = MutableStateFlow(0)
    val record = MutableStateFlow(
        RecordJuego(0, "") //

    )

    // Duraciones para animaciones de luz
    private val duracionEncendido = 500L
    private val duracionIntermitencia = 250L
    private val duracionInicial = 900L

    init {
        Log.d(TAG_LOG, "Inicializando el ViewModel - Estado: ${Estados.Inicio}")
        cargarRecordGuardado() // Al iniciar, cargar el récord guardado
    }

    private fun cargarRecordGuardado() {
        record.value = repositorio.recogerRecord()
        Log.d(TAG_LOG, "Record cargado: ${record.value.score}")
    }

    /**
     * Inicia el juego: reinicia valores, crea el primer color y muestra la secuencia.
     */
    fun empezarJuego() {
        ronda.value = 1
        secuencia.value.clear()
        secuenciaJugador.value.clear()
        generarColor()
        reproducirSecuencia()
    }

    /**
     * Añade un nuevo color aleatorio a la secuencia.
     */
    private fun generarColor() {
        val numero = (0..3).random()
        secuencia.value.add(numero)
        estadoActual.value = Estados.GenerarSecuencia
        Log.d(TAG_LOG, "Generando secuencia")
    }

    /**
     * Reproduce la secuencia: ilumina cada botón por turnos.
     */
    fun reproducirSecuencia() {
        viewModelScope.launch {
            estadoActual.value = Estados.GenerarSecuencia
            botonEncendido.value = null
            delay(duracionInicial)

            // Recorre la secuencia mostrando uno a uno
            for (color in secuencia.value) {
                botonEncendido.value = color
                delay(duracionEncendido)
                botonEncendido.value = null
                delay(duracionIntermitencia)
            }

            estadoActual.value = Estados.IntroducirSecuencia
        }
    }

    /**
     * Registra un color que el jugador ha pulsado y lo valida.
     */
    fun introducirSecuencia(color: Int) {
        // Si no es el turno del jugador, se ignora
        if (estadoActual.value != Estados.IntroducirSecuencia) {
            Log.d(TAG_LOG, "Aún no es tu turno")
            return
        }

        viewModelScope.launch {

            // Pequeño feedback luminoso al pulsar
            botonEncendido.value = color
            delay(200L)
            botonEncendido.value = null

            // Registrar la elección
            secuenciaJugador.value.add(color)
            val index = secuenciaJugador.value.lastIndex

            // Validar: si falla, fin del juego
            if (secuenciaJugador.value[index] != secuencia.value[index]) {
                gameOver()
                return@launch
            }

            // Si completa toda la secuencia correcta, avanzar ronda
            if (secuenciaJugador.value.size == secuencia.value.size) {
                ronda.value += 1
                secuenciaJugador.value.clear()
                generarColor()
                reproducirSecuencia()
            }
        }
    }

    /**
     * Termina el juego y actualiza récord si corresponde.
     */
    private fun gameOver() {
        estadoActual.value = Estados.GameOver

        // CAMBIO 5: Lógica de guardar récord
        val puntuacionActual = ronda.value
        val recordActual = record.value.score

        if (puntuacionActual > recordActual) {
            // Creamos el nuevo objeto con la puntuación y la fecha actual
            val nuevoRecord = RecordJuego(
                score = puntuacionActual,
                fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            )

            repositorio.guardarRecord(nuevoRecord)  // Si el nuevo record es mejo se guarda

            // Actualizamos el estado para la UI
            record.value = nuevoRecord
            botonEncendido.value = null // apagar luces por si acaso
            Log.d(TAG_LOG, "GAME OVER. Ronda alcanzada: ${ronda.value}")
        }
    }

        /**
         * Vuelve al estado inicial sin empezar una nueva partida.
         */
        fun resetToInicio() {
            estadoActual.value = Estados.Inicio
            ronda.value = 0
            secuencia.value.clear()
            secuenciaJugador.value.clear()
            botonEncendido.value = null
        }

    }