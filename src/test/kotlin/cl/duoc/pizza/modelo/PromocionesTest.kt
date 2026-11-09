package cl.duoc.pizza.modelo

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PromocionesTest {
    private val cliente: Cliente = Cliente(1, "Ana", "ana@duoc.cl", "hash", "+56 (912) 345-6789", "Av. Siempre Viva 123")
    private val margarita: Pizza = Pizza(1, "Margarita", 10000, Tamano.MEDIANA)
    private val bebida: Bebida = Bebida(2, "Agua", 1000)
    private val combo: Combo = Combo(3, "Combo Personal", 10000, mapOf(margarita to 1, bebida to 1))

    @Test
    fun `el rango de fechas incluye ambos extremos`() {
        assertTrue(Promociones.vigente(LocalDate.of(2026, 11, 1)))
        assertTrue(Promociones.vigente(LocalDate.of(2026, 11, 30)))
        assertFalse(Promociones.vigente(LocalDate.of(2026, 10, 31)))
        assertFalse(Promociones.vigente(LocalDate.of(2026, 12, 1)))
    }

    @Test
    fun `las anotaciones se leen con reflection`() {
        assertEquals(10, margarita.promocion?.porcentaje)
        assertEquals(15, combo.promocion?.porcentaje)
        assertNull(bebida.promocion)
        assertFalse(bebida.esPromocionable)
    }

    @Test
    fun `dentro del periodo descuenta solo los productos promocionables`() {
        val cantidades: Map<Producto, Int> = mapOf(margarita to 2, bebida to 3, combo to 1)
        // 10 % de 20.000 + 15 % de 10.000; la bebida no es promocionable
        assertEquals(2000 + 1500, Promociones.descuentoDe(cantidades, LocalDate.of(2026, 11, 15)))
        assertEquals(0, Promociones.descuentoDe(cantidades, LocalDate.of(2026, 12, 15)))
    }

    @Test
    fun `el total del pedido aplica la promocion segun su fecha`() {
        val enPromocion = Pedido(1, cliente, LocalDateTime.of(2026, 11, 15, 20, 0)).apply { agregar(margarita, 1) }
        val fueraDePromocion = Pedido(2, cliente, LocalDateTime.of(2026, 12, 15, 20, 0)).apply { agregar(margarita, 1) }

        // 10.000 - 1.000 de promoción = 9.000 + IVA 1.710
        assertEquals(Totales(10000, 0, 1710, 10710, promocion = 1000), enPromocion.calcularTotal())
        assertEquals(Totales(10000, 0, 1900, 11900), fueraDePromocion.calcularTotal())
        assertTrue(enPromocion.enPromocion)
    }
}
