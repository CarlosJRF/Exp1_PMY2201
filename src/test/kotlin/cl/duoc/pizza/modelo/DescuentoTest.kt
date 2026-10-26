package cl.duoc.pizza.modelo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DescuentoTest {
    private val usuario = Usuario(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123")
    private val pizza = Producto(1, "Margarita", Categoria.PIZZA, 9990, Tamano.MEDIANA)
    private val pizzaFamiliar = Producto(2, "Hawaiana", Categoria.PIZZA, 15490, Tamano.FAMILIAR)
    private val bebida = Producto(3, "Coca-Cola 1.5L", Categoria.BEBIDA, 2490)

    @Test
    fun `sin condiciones no hay descuento y el total es el subtotal`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(pizza, 2)
        pedido.agregar(bebida, 1)

        assertNull(pedido.descuento)
        assertEquals(22470, pedido.subtotal)
        assertEquals(22470, pedido.total)
    }

    @Test
    fun `tres pizzas aplican 15 por ciento`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(pizza, 3)

        assertEquals(15, pedido.descuento?.porcentaje)
        assertEquals(4496, pedido.descuento?.monto) // 29.970 * 15 % = 4.495,5 -> 4.496
        assertEquals(29970 - 4496, pedido.total)
    }

    @Test
    fun `subtotal desde 30000 aplica 10 por ciento`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(pizzaFamiliar, 2) // 30.980, solo 2 pizzas

        assertEquals(10, pedido.descuento?.porcentaje)
        assertEquals("compra desde $30.000", pedido.descuento?.descripcion)
        assertEquals(3098, pedido.descuento?.monto)
        assertEquals(27882, pedido.total)
    }

    @Test
    fun `justo bajo el limite de monto no hay descuento`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(Producto(4, "Combo", Categoria.ACOMPANAMIENTO, 29999), 1)

        assertNull(pedido.descuento)
    }

    @Test
    fun `si se cumplen ambas reglas se aplica solo la mayor`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(pizzaFamiliar, 3) // 46.470 y 3 pizzas

        assertEquals(15, pedido.descuento?.porcentaje)
        assertEquals(6971, pedido.descuento?.monto) // 6.970,5 -> 6.971
        assertEquals(46470 - 6971, pedido.total)
    }

    @Test
    fun `las bebidas no cuentan como pizzas`() {
        val pedido = Pedido(1, usuario)
        pedido.agregar(pizza, 2)
        pedido.agregar(bebida, 5)

        // 7 productos, pero solo 2 pizzas: aplica la regla de monto (32.430), no la de pizzas
        assertEquals(10, pedido.descuento?.porcentaje)
    }

    @Test
    fun `el porcentaje se redondea al peso mas cercano`() {
        assertEquals(1, Descuento.porcentajeDe(5, 10))  // 0,5 -> 1
        assertEquals(0, Descuento.porcentajeDe(4, 10))  // 0,4 -> 0
        assertEquals(1000, Descuento.porcentajeDe(10000, 10))
    }
}
