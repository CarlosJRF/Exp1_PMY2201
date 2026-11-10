package cl.duoc.pizza.servicio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidacionesTest {

    @Test
    fun `acepta correos con formato nombre@dominio`() {
        listOf("ana@duoc.cl", "ana.perez+pizza@mail.duoc.cl", "luis_99@dominio.com").forEach {
            assertTrue(Validaciones.esEmailValido(it), it)
        }
    }

    @Test
    fun `rechaza correos sin formato estandar`() {
        listOf("ana", "ana@", "@duoc.cl", "ana@duoc", "ana@duoc.c", "ana perez@duoc.cl", "ana@@duoc.cl").forEach {
            assertFalse(Validaciones.esEmailValido(it), it)
        }
    }

    @Test
    fun `formatea telefonos con estilo uniforme`() {
        assertEquals("+56 (912) 345-6789", Validaciones.formatearTelefono("9123456789"))
        assertEquals("+56 (912) 345-6789", Validaciones.formatearTelefono(" 912 345 6789 "))
        assertEquals("+56 (912) 345-6789", Validaciones.formatearTelefono("+56 912-345-6789"))
        assertEquals("+54 (911) 234-5678", Validaciones.formatearTelefono("(54) 911.234.5678"))
    }

    @Test
    fun `acepta celulares chilenos de 9 digitos con o sin codigo de pais`() {
        assertEquals("+56 9 1234-5678", Validaciones.formatearTelefono("9 1234 5678"))
        assertEquals("+56 9 1234-5678", Validaciones.formatearTelefono("912345678"))
        assertEquals("+56 9 1234-5678", Validaciones.formatearTelefono("56 9 1234 5678"))
        assertEquals("+56 9 1234-5678", Validaciones.formatearTelefono("+56912345678"))
    }

    @Test
    fun `no formatea telefonos con letras o largo incorrecto`() {
        listOf("abc", "12345678", "54912345678", "1234567890123", "91234x5678", "").forEach {
            assertNull(Validaciones.formatearTelefono(it), it)
        }
    }

    @Test
    fun `la cantidad debe estar entre 1 y 100`() {
        assertTrue(Validaciones.esCantidadValida(1))
        assertTrue(Validaciones.esCantidadValida(100))
        assertFalse(Validaciones.esCantidadValida(0))
        assertFalse(Validaciones.esCantidadValida(101))
        val error = assertFailsWith<ErrorNegocio> { Validaciones.validarCantidad(150) }
        assertEquals("La cantidad debe estar entre 1 y 100 (ingresaste 150)", error.message)
    }
}
