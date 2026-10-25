package cl.duoc.pizza.util

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatoTest {
    @Test
    fun `formatea precios en pesos chilenos`() {
        assertEquals("$0", Formato.precio(0))
        assertEquals("$990", Formato.precio(990))
        assertEquals("$12.990", Formato.precio(12990))
        assertEquals("$1.234.567", Formato.precio(1234567))
    }

    @Test
    fun `formatea fechas`() {
        assertEquals("05-03-2026 09:07", Formato.fecha(LocalDateTime.of(2026, 3, 5, 9, 7)))
    }
}
