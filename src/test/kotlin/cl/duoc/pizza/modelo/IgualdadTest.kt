package cl.duoc.pizza.modelo

import cl.duoc.pizza.repositorio.ProductoRepositorio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** equals() y hashCode() de Cliente y Producto, y cómo evitan duplicados en colecciones. */
class IgualdadTest {

    @Test
    fun `clientes con el mismo nombre y email son iguales aunque cambie el resto`() {
        val ana = Cliente(1, "Ana", "ana@duoc.cl", "hash1", "+56 (912) 345-6789", "Av. Siempre Viva 123")
        val otraAna = Cliente(7, " ana ", "ANA@duoc.cl", "hash2", "+56 (900) 000-0000", "Otra dirección")
        val luis = Cliente(2, "Luis", "ana@duoc.cl", "hash3", "+56 (987) 654-3210", "Los Aromos 45")

        assertEquals(ana, otraAna)
        assertEquals(ana.hashCode(), otraAna.hashCode())
        assertNotEquals(ana, luis)
        assertEquals(2, hashSetOf(ana, otraAna, luis).size)
    }

    @Test
    fun `productos con el mismo nombre y categoria son iguales y no se repiten en un Set`() {
        val margarita = Pizza(1, "Margarita", 9990, Tamano.MEDIANA)
        val copia = Pizza(50, "MARGARITA", 10990, Tamano.MEDIANA)
        assertEquals(margarita.hashCode(), copia.hashCode())
        assertEquals(1, setOf<Producto>(margarita, copia).size)
    }

    @Test
    fun `el catalogo rechaza productos repetidos`() {
        val repositorio = ProductoRepositorio(listOf(Bebida(1, "Agua", 1290)))
        assertFalse(repositorio.agregar(Bebida(2, "agua", 990)))      // mismo producto
        assertFalse(repositorio.agregar(Acompanamiento(1, "Papas", 2990))) // id repetido
        assertTrue(repositorio.agregar(Acompanamiento(3, "Agua", 990)))  // otra categoría
        assertEquals(2, repositorio.listar().size)
        assertFailsWith<IllegalArgumentException> { ProductoRepositorio(listOf(Bebida(1, "Agua", 1), Bebida(2, "Agua", 2))) }
    }
}
