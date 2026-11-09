package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Administrador
import cl.duoc.pizza.modelo.ContactoPredeterminado
import cl.duoc.pizza.repositorio.DatosIniciales
import cl.duoc.pizza.repositorio.UsuarioRepositorio
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class UsuarioServicioTest {
    private val servicio = UsuarioServicio(UsuarioRepositorio(listOf(DatosIniciales.admin)))

    private fun registrarAna(
        nombre: String = "Ana Pérez",
        email: String = "ana@duoc.cl",
        password: String = "secreto",
        telefono: String = "9123456789",
        direccion: String = "Av. Siempre Viva 123",
    ) = servicio.registrar(nombre, email, password, telefono, direccion)

    @Test
    fun `registro valido guarda el usuario con la contrasena en hash`() {
        val usuario = registrarAna(email = "  Ana@Duoc.cl ")

        assertEquals("ana@duoc.cl", usuario.email)
        assertEquals("+56 (912) 345-6789", usuario.telefono)
        assertNotEquals("secreto", usuario.passwordHash)
        assertTrue(usuario.passwordHash.startsWith("\$argon2id\$"))
        assertTrue(usuario.validarPassword("secreto"))
    }

    @Test
    fun `no permite registrar un email repetido`() {
        registrarAna()
        val error = assertFailsWith<ErrorNegocio> { registrarAna(email = "ANA@duoc.cl") }
        assertEquals("Ya existe un usuario con ese email", error.message)
    }

    @Test
    fun `valida los campos obligatorios del registro`() {
        assertFailsWith<ErrorNegocio> { registrarAna(nombre = " ") }
        assertFailsWith<ErrorNegocio> { registrarAna(password = "123") }
        assertFailsWith<ErrorNegocio> { registrarAna(direccion = "") }
    }

    @Test
    fun `email y telefono invalidos se reemplazan por valores predeterminados`() {
        val ana = registrarAna(email = "ana-sin-arroba", telefono = "abc")
        assertEquals(ContactoPredeterminado.EMAIL, ana.email)
        assertEquals(ContactoPredeterminado.TELEFONO, ana.telefono)

        val error = assertFailsWith<ErrorNegocio> { registrarAna(email = "otro-invalido") }
        assertContains(error.message!!, "ingresa un email correcto")
    }

    @Test
    fun `inicia sesion con credenciales correctas`() {
        val ana = registrarAna()
        assertEquals(ana, servicio.iniciarSesion("ana@duoc.cl", "secreto"))
        assertIs<Administrador>(servicio.iniciarSesion("admin@duocpizza.cl", "admin123"))
    }

    @Test
    fun `rechaza credenciales incorrectas`() {
        registrarAna()
        assertFailsWith<ErrorNegocio> { servicio.iniciarSesion("ana@duoc.cl", "otra") }
        assertFailsWith<ErrorNegocio> { servicio.iniciarSesion("nadie@duoc.cl", "secreto") }
    }
}
