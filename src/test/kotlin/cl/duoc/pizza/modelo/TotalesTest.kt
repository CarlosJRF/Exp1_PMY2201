package cl.duoc.pizza.modelo

import kotlin.test.Test
import kotlin.test.assertEquals

class TotalesTest {

    @Test
    fun `sin descuento suma el IVA al subtotal`() {
        // 22.470 * 19 % = 4.269,3 -> 4.269
        assertEquals(Totales(22470, 0, 4269, 26739), calcularTotal(22470, 3))
    }

    @Test
    fun `mas de 3 pizzas aplica 10 por ciento antes del IVA`() {
        // 39.960 - 3.996 = 35.964; IVA 6.833,16 -> 6.833
        assertEquals(Totales(39960, 3996, 6833, 42797), calcularTotal(39960, 4))
    }

    @Test
    fun `el pedido cuenta solo las pizzas para el descuento`() {
        val pedido = Pedido(1, Cliente(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123"))
        pedido.agregar(Producto(1, "Margarita", Categoria.PIZZA, 9990, Tamano.MEDIANA), 3)
        pedido.agregar(Producto(2, "Coca-Cola 1.5L", Categoria.BEBIDA, 2490), 5)

        assertEquals(3, pedido.cantidadPizzas)
        assertEquals(0, pedido.calcularTotal().descuento)
    }

    @Test
    fun `el porcentaje se redondea al peso mas cercano`() {
        assertEquals(1, porcentajeDe(5, 10))  // 0,5 -> 1
        assertEquals(0, porcentajeDe(4, 10))  // 0,4 -> 0
        assertEquals(1000, porcentajeDe(10000, 10))
    }
}
