package cl.duoc.pizza.servicio

import cl.duoc.pizza.repositorio.DatosIniciales
import cl.duoc.pizza.repositorio.UsuarioRepositorio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class UsuarioServicioTest {
    private val servicio = UsuarioServicio(UsuarioRepositorio(listOf(DatosIniciales.admin)))

    private fun registrarAna(
        nombre: String = "Ana Pérez",
        email: String = "ana@duoc.cl",
        password: String = "secreto",
        telefono: String = "912345678",
        direccion: String = "Av. Siempre Viva 123",
    ) = servicio.registrar(nombre, email, password, telefono, direccion)

    @Test
    fun `registro valido guarda el usuario con la contrasena en hash`() {
        val usuario = registrarAna(email = "  Ana@Duoc.cl ")

        assertEquals("ana@duoc.cl", usuario.email)
        assertNotEquals("secreto", usuario.passwordHash)
        assertEquals(64, usuario.passwordHash.length)
        assertTrue(!usuario.esAdmin)
    }

    @Test
    fun `no permite registrar un email repetido`() {
        registrarAna()
        val error = assertFailsWith<ErrorNegocio> { registrarAna(email = "ANA@duoc.cl") }
        assertEquals("Ya existe un usuario con ese email", error.message)
    }

    @Test
    fun `valida los campos del registro`() {
        assertFailsWith<ErrorNegocio> { registrarAna(nombre = " ") }
        assertFailsWith<ErrorNegocio> { registrarAna(email = "ana-sin-arroba") }
        assertFailsWith<ErrorNegocio> { registrarAna(password = "123") }
        assertFailsWith<ErrorNegocio> { registrarAna(telefono = "abc") }
        assertFailsWith<ErrorNegocio> { registrarAna(direccion = "") }
    }

    @Test
    fun `inicia sesion con credenciales correctas`() {
        val ana = registrarAna()
        assertEquals(ana, servicio.iniciarSesion("ana@duoc.cl", "secreto"))
        assertTrue(servicio.iniciarSesion("admin@duocpizza.cl", "admin123").esAdmin)
    }

    @Test
    fun `rechaza credenciales incorrectas`() {
        registrarAna()
        assertFailsWith<ErrorNegocio> { servicio.iniciarSesion("ana@duoc.cl", "otra") }
        assertFailsWith<ErrorNegocio> { servicio.iniciarSesion("nadie@duoc.cl", "secreto") }
    }
}
