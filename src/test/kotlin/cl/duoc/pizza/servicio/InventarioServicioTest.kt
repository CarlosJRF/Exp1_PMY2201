package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
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
        val faltantes = inventario.evaluarDisponibilidad(mapOf(pizza to 4, bebida to 1))

        assertEquals(listOf("Queso", "Agua"), faltantes.map { it.nombre })
        val mensaje = inventario.mensajeFaltantes(faltantes)
        assertContains(mensaje, "Queso: hay 6, se necesitan 8 (faltan 2)")
        assertContains(mensaje, "Agua: agotado (se necesitan 1)")
        assertContains(mensaje, "Reponer al menos: Queso (2), Agua (1)")
    }

    @Test
    fun `descontar usa los ingredientes y no deja stock negativo`() {
        inventario.descontar(mapOf(pizza to 2))
        assertEquals(3, inventario.stockDe("Masa"))
        assertEquals(2, inventario.stockDe("Queso"))

        assertFailsWith<ErrorNegocio> { inventario.descontar(mapOf(pizza to 2)) }
        assertEquals(3, inventario.stockDe("Masa"))
    }

    @Test
    fun `preparar un lote calcula los ingredientes y actualiza el inventario`() {
        assertEquals(mapOf("Masa" to 2, "Queso" to 4), inventario.cantidadParaLote(pizza.ingredientes, 2))

        val usados = inventario.prepararLote(pizza.ingredientes, 3)
        assertEquals(mapOf("Masa" to 3, "Queso" to 6), usados)
        assertEquals(2, inventario.stockDe("Masa"))
        assertEquals(0, inventario.stockDe("Queso"))

        val error = assertFailsWith<ErrorNegocio> { inventario.prepararLote(pizza.ingredientes, 1) }
        assertContains(error.message!!, "Queso: agotado")
        assertEquals(2, inventario.stockDe("Masa"))
        assertFailsWith<ErrorNegocio> { inventario.prepararLote(pizza.ingredientes, 0) }
    }

    @Test
    fun `reponer aumenta el stock del ingrediente`() {
        inventario.reponer("Agua", 10)
        assertEquals(10, inventario.stockDe("Agua"))
        assertFailsWith<ErrorNegocio> { inventario.reponer("Agua", 0) }
        val negativa = assertFailsWith<ErrorNegocio> { inventario.reponer("Agua", -3) }
        assertContains(negativa.message!!, "no puede ser negativa (-3)")
        assertEquals(10, inventario.stockDe("Agua"))
        assertFailsWith<ErrorNegocio> { inventario.reponer("Harina", 5) }
    }

    @Test
    fun `detecta ingredientes con stock bajo y agotados`() {
        assertEquals(listOf("Masa", "Agua"), inventario.ingredientesConStockBajo())
        assertEquals(setOf("Agua"), inventario.ingredientesAgotados())
    }

    @Test
    fun `si falta un ingrediente no se descuenta ninguno`() {
        val error = assertFailsWith<StockInsuficiente> { inventario.descontar(mapOf(pizza to 2, bebida to 1)) }
        assertEquals(listOf("Agua"), error.faltantes.map { it.nombre })
        assertEquals(5, inventario.stockDe("Masa"))
        assertEquals(6, inventario.stockDe("Queso"))
    }

    @Test
    fun `buscar producto inexistente lanza error`() {
        assertEquals(pizza, inventario.buscarProducto(1))
        assertFailsWith<ErrorNegocio> { inventario.buscarProducto(99) }
    }
}
