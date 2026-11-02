package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.ItemPedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.repositorio.ProductoRepositorio
import cl.duoc.pizza.repositorio.StockRepositorio
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InventarioServicioTest {
    private val pizza = Producto(1, "Margarita", Categoria.PIZZA, 9990, ingredientes = mapOf("Masa" to 1, "Queso" to 2))
    private val bebida = Producto(2, "Agua", Categoria.BEBIDA, 1290)
    private val inventario = InventarioServicio(
        ProductoRepositorio(listOf(pizza, bebida)),
        StockRepositorio(mapOf("Masa" to 5, "Queso" to 6, "Agua" to 0)),
    )

    @Test
    fun `calcula las unidades que alcanzan con los ingredientes`() {
        assertEquals(3, inventario.unidadesDisponibles(pizza))
        assertEquals(1, inventario.unidadesDisponibles(pizza, reservado = mapOf("Queso" to 4)))
        assertTrue(inventario.hayStock(pizza, 3))
        assertFalse(inventario.hayStock(pizza, 4))
        assertEquals("AGOTADO", inventario.disponibilidad(bebida))
    }

    @Test
    fun `evalua cada ingrediente con when`() {
        assertEquals(NivelIngrediente.SUFICIENTE, inventario.nivelDe("Masa", 5))
        assertEquals(NivelIngrediente.INSUFICIENTE, inventario.nivelDe("Queso", 8))
        assertEquals(NivelIngrediente.AGOTADO, inventario.nivelDe("Agua", 1))
    }

    @Test
    fun `informa los ingredientes insuficientes y como reabastecerlos`() {
        val faltantes = inventario.evaluarDisponibilidad(listOf(ItemPedido(pizza, 4), ItemPedido(bebida, 1)))

        assertEquals(listOf("Queso", "Agua"), faltantes.map { it.nombre })
        val mensaje = inventario.mensajeFaltantes(faltantes)
        assertContains(mensaje, "Queso: hay 6, se necesitan 8 (faltan 2)")
        assertContains(mensaje, "Agua: agotado (se necesitan 1)")
        assertContains(mensaje, "Reponer al menos: Queso (2), Agua (1)")
    }

    @Test
    fun `descontar usa los ingredientes y no deja stock negativo`() {
        inventario.descontar(listOf(ItemPedido(pizza, 2)))
        assertEquals(3, inventario.stockDe("Masa"))
        assertEquals(2, inventario.stockDe("Queso"))

        assertFailsWith<ErrorNegocio> { inventario.descontar(listOf(ItemPedido(pizza, 2))) }
        assertEquals(3, inventario.stockDe("Masa"))
    }

    @Test
    fun `reponer aumenta el stock del ingrediente`() {
        inventario.reponer("Agua", 10)
        assertEquals(10, inventario.stockDe("Agua"))
        assertFailsWith<ErrorNegocio> { inventario.reponer("Agua", 0) }
        assertFailsWith<ErrorNegocio> { inventario.reponer("Harina", 5) }
    }

    @Test
    fun `detecta ingredientes con stock bajo`() {
        assertEquals(listOf("Masa", "Agua"), inventario.ingredientesConStockBajo())
    }

    @Test
    fun `buscar producto inexistente lanza error`() {
        assertEquals(pizza, inventario.buscarProducto(1))
        assertFailsWith<ErrorNegocio> { inventario.buscarProducto(99) }
    }
}
