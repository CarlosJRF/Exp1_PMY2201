package cl.duoc.pizza.modelo

import cl.duoc.pizza.repositorio.DatosIniciales
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ProductoTest {

    @Test
    fun `el nombre de la pizza incluye el tamano`() {
        assertEquals("Margarita Mediana", Pizza(1, "Margarita", 9990, Tamano.MEDIANA).nombre)
        assertEquals("Margarita", Pizza(1, "Margarita", 9990).nombre)
    }

    @Test
    fun `el combo suma los ingredientes de sus componentes`() {
        val pizza = Pizza(1, "Margarita", 5990, Tamano.PERSONAL, mapOf("Masa" to 1, "Queso" to 1))
        val agua = Bebida(2, "Agua", 1290)
        val combo = Combo(3, "Combo Personal", 6490, mapOf(pizza to 2, agua to 1))

        assertEquals(mapOf("Masa" to 2, "Queso" to 2, "Agua" to 1), combo.ingredientes)
        assertEquals("Combo Personal (\$6.490) · Combo · Incluye: 2 x Margarita Personal, 1 x Agua", combo.detallarProducto())
    }

    @Test
    fun `el catalogo incluye combos y no tiene productos repetidos`() {
        val productos: List<Producto> = DatosIniciales.productos
        assertEquals(productos.size, productos.toSet().size)
        assertTrue(productos.any { it is Combo })
        assertNotEquals(productos.first { it.id == 2 }, productos.first { it.id == 3 }) // Margarita Mediana != Familiar
    }
}
