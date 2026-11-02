package cl.duoc.pizza.modelo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PedidoTest {
    private val usuario = Cliente(1, "Ana", "ana@duoc.cl", "secreto", "912345678", "Av. Siempre Viva 123")
    private val margarita = Producto(1, "Margarita", Categoria.PIZZA, 8990, Tamano.MEDIANA)
    private val bebida = Producto(2, "Coca-Cola 1.5L", Categoria.BEBIDA, 2490)

    @Test
    fun `total suma los subtotales de los items mas IVA`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(margarita, 2)
        pedido.agregar(bebida, 1)

        assertEquals(8990 * 2 + 2490, pedido.subtotal)
        assertEquals(20470 + 3889, pedido.total) // IVA: 3.889,3 -> 3.889
    }

    @Test
    fun `agregar el mismo producto acumula la cantidad`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(margarita, 1)
        pedido.agregar(margarita, 2)

        assertEquals(1, pedido.cantidades.size)
        assertEquals(3, pedido.productos.size)
        assertEquals(3, pedido.cantidadDe(margarita))
    }

    @Test
    fun `quitar toda la cantidad elimina el producto`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(margarita, 2)
        pedido.quitar(margarita, 1)
        assertEquals(1, pedido.cantidadDe(margarita))

        pedido.quitar(margarita, 5)
        assertTrue(pedido.estaVacio)
    }

    @Test
    fun `no permite cantidades invalidas`() {
        val pedido = Pedido(1, usuario)
        assertFailsWith<IllegalArgumentException> { pedido.agregar(margarita, 0) }
        assertFailsWith<IllegalArgumentException> { pedido.quitar(bebida, 1) }
    }

    @Test
    fun `el estado cambia solo en orden`() {
        val pedido = Pedido(1, usuario)
        assertFailsWith<IllegalArgumentException> { pedido.cambiarEstado(EstadoPedido.LISTO) }
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION)
        pedido.cambiarEstado(EstadoPedido.LISTO)
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        assertEquals("En camino", pedido.estadoVisible)
        assertFailsWith<IllegalArgumentException> { pedido.cambiarEstado(EstadoPedido.EN_CAMINO) }
    }

    @Test
    fun `detallar producto incluye precio e ingredientes de las pizzas`() {
        val hawaiana = Producto(3, "Hawaiana", Categoria.PIZZA, 11490, Tamano.MEDIANA, mapOf("Masa" to 2, "Piña" to 2))
        assertEquals("Hawaiana Mediana ($11.490) · Pizza · Ingredientes: Masa, Piña", hawaiana.detallarProducto())
        assertEquals("Coca-Cola 1.5L ($2.490) · Bebida", bebida.detallarProducto())
    }

    @Test
    fun `nombre completo incluye el tamano`() {
        assertEquals("Margarita Mediana", margarita.nombreCompleto)
        assertEquals("Coca-Cola 1.5L", bebida.nombreCompleto)
    }
}
