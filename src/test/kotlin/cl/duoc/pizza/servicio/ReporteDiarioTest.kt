package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Tamano
import cl.duoc.pizza.modelo.Usuario
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ReporteDiarioTest {
    private val ana = Usuario(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123")
    private val luis = Usuario(2, "Luis", "luis@duoc.cl", "hash", "987654321", "Los Aromos 45")
    private val margarita = Producto(1, "Margarita", Categoria.PIZZA, 9990, Tamano.MEDIANA)
    private val bebida = Producto(2, "Agua", Categoria.BEBIDA, 1290)

    private fun pedido(id: Int, usuario: Usuario, estado: EstadoPedido): Pedido =
        Pedido(id, usuario).apply {
            agregar(margarita, 1)
            agregar(bebida, 1)
            this.estado = estado
        }

    private val pedidos: Array<Pedido> = arrayOf(
        pedido(1, ana, EstadoPedido.EN_PREPARACION),
        pedido(2, luis, EstadoPedido.LISTO),
        pedido(3, ana, EstadoPedido.EN_CAMINO),
    )

    @Test
    fun `agrupa completados y pendientes`() {
        assertEquals(listOf(2, 3), ReporteDiario.completados(pedidos).map { it.id })
        assertEquals(listOf(1), ReporteDiario.pendientes(pedidos).map { it.id })
    }

    @Test
    fun `el reporte incluye cliente, pizzas, total y estado de cada pedido`() {
        val reporte = ReporteDiario.generar(pedidos, LocalDate.of(2026, 11, 2))

        assertContains(reporte, "Reporte del día 02-11-2026: 3 pedido(s)")
        assertContains(reporte, "#2  Cliente: Luis")
        assertContains(reporte, "Pizzas: 1 x Margarita Mediana")
        assertContains(reporte, "Total: $11.280  Estado: Listo")
        assertContains(reporte, "Completados (2): #2, #3")
        assertContains(reporte, "Pendientes (1): #1")
    }
}
