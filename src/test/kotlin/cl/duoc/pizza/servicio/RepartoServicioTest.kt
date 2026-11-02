package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Repartidor
import cl.duoc.pizza.modelo.Cliente
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RepartoServicioTest {
    private val usuario = Cliente(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123")
    private val lejos = Repartidor(1, "Pedro", "+56911111111", "pedro@duocpizza.cl", 3.0)
    private val cerca = Repartidor(2, "Camila", "+56922222222", "camila@duocpizza.cl", 1.0)
    private val reparto = RepartoServicio(listOf(lejos, cerca))

    private fun pedidoListo(id: Int): Pedido = Pedido(id, usuario).apply {
        agregar(Producto(1, "Margarita", Categoria.PIZZA, 9990), 1)
        cambiarEstado(EstadoPedido.EN_PREPARACION)
        cambiarEstado(EstadoPedido.LISTO)
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
        val pedido = Pedido(1, usuario).apply { agregar(Producto(1, "Margarita", Categoria.PIZZA, 9990), 1) }
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION)
        assertFailsWith<ErrorNegocio> { reparto.asignar(pedido) }
    }

    @Test
    fun `al entregar el pedido el repartidor queda disponible otra vez`() {
        val pedido = pedidoListo(1)
        assertFailsWith<ErrorNegocio> { reparto.entregar(pedido) }

        reparto.asignar(pedido)
        assertSame(cerca, reparto.entregar(pedido))
        assertTrue(pedido.entregado)
        assertTrue(cerca.disponible)
        assertFailsWith<ErrorNegocio> { reparto.entregar(pedido) }
        assertSame(cerca, reparto.asignar(pedidoListo(2)))
    }
}
