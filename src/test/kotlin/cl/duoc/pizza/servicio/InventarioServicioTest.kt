package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.repositorio.ProductoRepositorio
import cl.duoc.pizza.repositorio.StockRepositorio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InventarioServicioTest {
    private val pizza = Producto(1, "Margarita", Categoria.PIZZA, 9990)
    private val bebida = Producto(2, "Agua", Categoria.BEBIDA, 1290)
    private val inventario = InventarioServicio(
        ProductoRepositorio(listOf(pizza, bebida)),
        StockRepositorio(mapOf(pizza.id to 5, bebida.id to 2)),
    )

    @Test
    fun `descontar reduce el stock`() {
        inventario.descontar(pizza, 3)
        assertEquals(2, inventario.stockDe(pizza))
    }

    @Test
    fun `no permite dejar el stock negativo`() {
        assertFailsWith<ErrorNegocio> { inventario.descontar(pizza, 6) }
        assertEquals(5, inventario.stockDe(pizza))
    }

    @Test
    fun `reponer aumenta el stock`() {
        inventario.reponer(bebida, 10)
        assertEquals(12, inventario.stockDe(bebida))
        assertFailsWith<ErrorNegocio> { inventario.reponer(bebida, 0) }
    }

    @Test
    fun `detecta stock bajo`() {
        assertTrue(inventario.hayStock(pizza, 5))
        assertFalse(inventario.hayStock(pizza, 6))
        assertEquals(listOf(bebida), inventario.productosConStockBajo())
    }

    @Test
    fun `buscar producto inexistente lanza error`() {
        assertEquals(pizza, inventario.buscarProducto(1))
        assertFailsWith<ErrorNegocio> { inventario.buscarProducto(99) }
    }
}
