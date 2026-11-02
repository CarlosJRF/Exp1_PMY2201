package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Repartidor
import cl.duoc.pizza.modelo.Usuario
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame

class RepartoServicioTest {
    private val usuario = Usuario(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123")
    private val lejos = Repartidor(1, "Pedro", "+56911111111", 3.0)
    private val cerca = Repartidor(2, "Camila", "+56922222222", 1.0)
    private val reparto = RepartoServicio(listOf(lejos, cerca))

    private fun pedidoListo(id: Int): Pedido = Pedido(id, usuario).apply {
        agregar(Producto(1, "Margarita", Categoria.PIZZA, 9990), 1)
        estado = EstadoPedido.LISTO
    }

    @Test
    fun `asigna el repartidor disponible mas cercano`() {
        val primero = pedidoListo(1)
        assertSame(cerca, reparto.asignar(primero))
        assertEquals(EstadoPedido.EN_CAMINO, primero.estado)
        assertSame(cerca, primero.repartidor)
        assertFalse(cerca.disponible)

        assertSame(lejos, reparto.asignar(pedidoListo(2)))
        assertFailsWith<ErrorNegocio> { reparto.asignar(pedidoListo(3)) }
    }

    @Test
    fun `solo asigna pedidos listos`() {
        val pedido = pedidoListo(1)
        pedido.estado = EstadoPedido.EN_PREPARACION
        assertFailsWith<ErrorNegocio> { reparto.asignar(pedido) }
    }
}
