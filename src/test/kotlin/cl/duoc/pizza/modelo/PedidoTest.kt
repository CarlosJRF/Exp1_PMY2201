package cl.duoc.pizza.modelo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PedidoTest {
    private val usuario = Usuario(1, "Ana", "ana@duoc.cl", "secreto", "912345678", "Av. Siempre Viva 123")
    private val margarita = Producto(1, "Margarita", Categoria.PIZZA, 8990, Tamano.MEDIANA)
    private val bebida = Producto(2, "Coca-Cola 1.5L", Categoria.BEBIDA, 2490)

    @Test
    fun `total suma los subtotales de los items`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(margarita, 2)
        pedido.agregar(bebida, 1)

        assertEquals(8990 * 2 + 2490, pedido.total)
    }

    @Test
    fun `agregar el mismo producto acumula la cantidad`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(margarita, 1)
        pedido.agregar(margarita, 2)

        assertEquals(1, pedido.items.size)
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
    fun `nombre completo incluye el tamano`() {
        assertEquals("Margarita Mediana", margarita.nombreCompleto)
        assertEquals("Coca-Cola 1.5L", bebida.nombreCompleto)
    }
}
