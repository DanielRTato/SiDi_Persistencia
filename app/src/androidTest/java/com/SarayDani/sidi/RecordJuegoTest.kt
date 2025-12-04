package com.SarayDani.sidi

import junit.framework.TestCase.assertEquals
import org.junit.Test

class RecordJuegoTest {

    @Test
    fun testCrearRecord() {
        val score = 5
        val fecha = "01/01/2111 12:00"
        val record = RecordJuego(score, fecha)

        assertEquals(50, record.score)
        assertEquals("01/01/2111 12:00", record.fecha)

    }
}