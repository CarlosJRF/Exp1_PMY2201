package cl.duoc.pizza.modelo

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** Sobrecarga de operadores (+ y ==) y desestructuración de Pedido y Cliente. */
class OperadoresTest {
    private val fecha: LocalDateTime = LocalDateTime.of(2026, 10, 15, 12, 0) // fuera de la promoción
    private val ana: Cliente = Cliente(1, "Ana", "ana@duoc.cl", "hash", "+56 (912) 345-6789", "Av. Siempre Viva 123")
    private val luis: Cliente = Cliente(2, "Luis", "luis@duoc.cl", "hash", "+56 (987) 654-3210", "Los Aromos 45")
    private val margarita: Pizza = Pizza(1, "Margarita", 10000, Tamano.MEDIANA)
    private val bebida: Bebida = Bebida(2, "Agua", 1000)

    @Test
    fun `mas combina los productos de dos pedidos y recalcula el total`() {
        val primero = Pedido(1, ana, fecha).apply { agregar(margarita, 2); agregar(bebida, 1) }
        val segundo = Pedido(2, ana, fecha).apply { agregar(margarita, 2) }

        val combinado: Pedido = primero + segundo

        assertEquals<Map<Producto, Int>>(mapOf(margarita to 4, bebida to 1), combinado.cantidades)
        assertEquals(1, combinado.id)
        // 41.000 - 10 % (más de 3 pizzas) = 36.900 + IVA 7.011
        assertEquals(Totales(41000, 4100, 7011, 43911), combinado.calcularTotal())
        assertEquals(2, primero.cantidadDe(margarita)) // los originales no cambian
    }

    @Test
    fun `no combina pedidos de clientes distintos`() {
        assertFailsWith<IllegalArgumentException> { Pedido(1, ana, fecha) + Pedido(2, luis, fecha) }
    }

    @Test
    fun `igual para productos compara nombre y categoria`() {
        assertEquals<Producto>(margarita, Pizza(99, "Margarita", 12990, Tamano.MEDIANA))
        assertNotEquals<Producto>(margarita, Pizza(1, "Margarita", 10000, Tamano.FAMILIAR))
        assertTrue(Bebida(5, "Agua", 1000) != Acompanamiento(6, "Agua", 1000))
    }

    @Test
    fun `desestructura un pedido en cliente, productos y total`() {
        val pedido = Pedido(1, ana, fecha).apply { agregar(margarita, 1) }
        val (cliente: Cliente, productos: Map<Producto, Int>, total: Int) = pedido
        assertEquals(ana, cliente)
        assertEquals<Map<Producto, Int>>(mapOf(margarita to 1), productos)
        assertEquals(11900, total)
    }

    @Test
    fun `desestructura un cliente en nombre, email y telefono`() {
        val (nombre: String, email: String, telefono: String) = ana
        assertEquals("Ana", nombre)
        assertEquals("ana@duoc.cl", email)
        assertEquals("+56 (912) 345-6789", telefono)
    }
}
