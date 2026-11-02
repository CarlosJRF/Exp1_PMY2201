package cl.duoc.pizza.modelo

import cl.duoc.pizza.util.Seguridad

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UsuarioTest {
    private val cliente: Cliente = Cliente(1, "Ana", "ana@duoc.cl", Seguridad.hashPassword("secreto"), "912345678", "Av. Siempre Viva 123")
    private val repartidor: Repartidor = Repartidor(1, "Pedro", "+56911111111", "pedro@duocpizza.cl", 2.5)
    private val admin: Administrador = Administrador(1, "Admin", "admin@duocpizza.cl", Seguridad.hashPassword("admin123"))

    @Test
    fun `cada tipo de usuario se describe a su manera`() {
        val usuarios: List<Usuario> = listOf(cliente, repartidor, admin)
        val descripciones: List<String> = usuarios.map { it.descripcion() }

        assertEquals("Cliente: Ana · 912345678 · ana@duoc.cl · Av. Siempre Viva 123", descripciones[0])
        assertEquals("Repartidor: Pedro · +56911111111 · pedro@duocpizza.cl · a 2.5 km · Disponible", descripciones[1])
        assertEquals("Administrador: Admin · sin teléfono · admin@duocpizza.cl", descripciones[2])
    }

    @Test
    fun `solo clientes y administradores pueden iniciar sesion`() {
        assertTrue(cliente.validarPassword("secreto"))
        assertFalse(cliente.validarPassword("otro"))
        assertTrue(admin.validarPassword("admin123"))
        assertFalse(admin.validarPassword("secreto"))
        assertFalse(repartidor.validarPassword("secreto"))
    }
}
