package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Tamano
import cl.duoc.pizza.modelo.Usuario
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class ResumenPedidoTest {
    private val usuario = Usuario(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123")
    private val margarita = Producto(1, "Margarita", Categoria.PIZZA, 9990, Tamano.MEDIANA)

    @Test
    fun `el resumen incluye datos del pedido, del cliente y del producto`() {
        val pedido = Pedido(7, usuario, LocalDateTime.of(2026, 10, 25, 20, 30))
        pedido.agregar(margarita, 2)

        val resumen = ResumenPedido.generar(pedido)

        assertContains(resumen, "Pedido #7  (Pendiente)")
        assertContains(resumen, "25-10-2026 20:30")
        assertContains(resumen, "Nombre:    Ana")
        assertContains(resumen, "Email:     ana@duoc.cl")
        assertContains(resumen, "Teléfono:  912345678")
        assertContains(resumen, "Dirección: Av. Siempre Viva 123")
        assertContains(resumen, "2 x Margarita Mediana ($9.990 c/u)")
        assertContains(resumen, "Unidades: 2")
        assertContains(resumen, "$19.980")
        assertContains(resumen, "TOTAL")
        assertFalse(resumen.contains("Descuento"))
    }

    @Test
    fun `el resumen muestra el descuento aplicado`() {
        val pedido = Pedido(8, usuario)
        pedido.agregar(margarita, 3)

        val resumen = ResumenPedido.generar(pedido)

        assertContains(resumen, "$29.970")
        assertContains(resumen, "Descuento 15% (3 o más pizzas)")
        assertContains(resumen, "-$4.496")
        assertContains(resumen, "$25.474")
    }
}
