package cl.duoc.pizza.modelo

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PedidoTest {
    private val FUERA_DE_PROMOCION: LocalDateTime = LocalDateTime.of(2026, 10, 15, 12, 0)
    private val usuario = Cliente(1, "Ana", "ana@duoc.cl", "secreto", "912345678", "Av. Siempre Viva 123")
    private val margarita = Pizza(1, "Margarita", 8990, Tamano.MEDIANA)
    private val bebida = Bebida(2, "Coca-Cola 1.5L", 2490)

    @Test
    fun `total suma los subtotales de los items mas IVA`() {
        val pedido = Pedido(1, usuario, FUERA_DE_PROMOCION)
        pedido.agregar(margarita, 2)
        pedido.agregar(bebida, 1)

        assertEquals(8990 * 2 + 2490, pedido.subtotal)
        assertEquals(20470 + 3889, pedido.total) // IVA: 3.889,3 -> 3.889
    }

    @Test
    fun `agregar el mismo producto acumula la cantidad`() {
        val pedido = Pedido(1, usuario, FUERA_DE_PROMOCION)
        pedido.agregar(margarita, 1)
        pedido.agregar(margarita, 2)

        assertEquals(1, pedido.cantidades.size)
        assertEquals<List<Producto>>(listOf(margarita), pedido.productos)
        assertEquals<Map<Producto, Int>>(mapOf(margarita to 3), pedido.cantidades)
        assertEquals(3, pedido.cantidadDe(margarita))
    }

    @Test
    fun `quitar toda la cantidad elimina el producto`() {
        val pedido = Pedido(1, usuario, FUERA_DE_PROMOCION)
        pedido.agregar(margarita, 2)
        pedido.quitar(margarita, 1)
        assertEquals(1, pedido.cantidadDe(margarita))

        pedido.quitar(margarita, 5)
        assertTrue(pedido.estaVacio)
    }

    @Test
    fun `no permite cantidades invalidas`() {
        val pedido = Pedido(1, usuario, FUERA_DE_PROMOCION)
        assertFailsWith<IllegalArgumentException> { pedido.agregar(margarita, 0) }
        assertFailsWith<IllegalArgumentException> { pedido.quitar(bebida, 1) }
    }

    @Test
    fun `las notas, el repartidor y la entrega solo cambian con sus validaciones`() {
        val pedido = Pedido(1, usuario, FUERA_DE_PROMOCION)
        pedido.agregar(margarita, 1)
        pedido.registrarNotas("  ")
        assertEquals(null, pedido.notas)
        assertFailsWith<IllegalArgumentException> { pedido.registrarNotas("x".repeat(Pedido.LARGO_MAXIMO_NOTAS + 1)) }

        val repartidor = Repartidor(1, "Pedro", "+56911111111", "pedro@duocpizza.cl", 2.0)
        assertFailsWith<IllegalArgumentException> { pedido.asignarRepartidor(repartidor) }
        assertFailsWith<IllegalArgumentException> { pedido.marcarEntregado() }

        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION)
        assertFailsWith<IllegalArgumentException> { pedido.registrarNotas("tarde") }
        pedido.cambiarEstado(EstadoPedido.LISTO)
        pedido.asignarRepartidor(repartidor)
        assertEquals(EstadoPedido.EN_CAMINO, pedido.estado)
        pedido.marcarEntregado()
        assertTrue(pedido.entregado)
        assertFailsWith<IllegalArgumentException> { pedido.marcarEntregado() }
    }

    @Test
    fun `el estado cambia solo en orden`() {
        val pedido = Pedido(1, usuario, FUERA_DE_PROMOCION)
        assertFailsWith<IllegalArgumentException> { pedido.cambiarEstado(EstadoPedido.LISTO) }
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION)
        pedido.cambiarEstado(EstadoPedido.LISTO)
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        assertEquals("En camino", pedido.estadoVisible)
        assertFailsWith<IllegalArgumentException> { pedido.cambiarEstado(EstadoPedido.EN_CAMINO) }
    }

    @Test
    fun `detallar producto incluye precio e ingredientes de las pizzas`() {
        val hawaiana = Pizza(3, "Hawaiana", 11490, Tamano.MEDIANA, mapOf("Masa" to 2, "Piña" to 2))
        assertEquals("Hawaiana Mediana ($11.490) · Pizza · Ingredientes: Masa, Piña", hawaiana.detallarProducto())
        assertEquals("Coca-Cola 1.5L ($2.490) · Bebida", bebida.detallarProducto())
    }

    @Test
    fun `el nombre de la pizza incluye el tamano`() {
        assertEquals("Margarita Mediana", margarita.nombre)
        assertEquals("Coca-Cola 1.5L", bebida.nombre)
    }
}
