package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Tamano
import cl.duoc.pizza.modelo.Usuario
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertContains

class ResumenPedidoTest {
    private val usuario = Usuario(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123")

    @Test
    fun `el resumen incluye detalle y total`() {
        val pedido = Pedido(7, usuario, LocalDateTime.of(2026, 10, 25, 20, 30))
        pedido.agregar(Producto(1, "Margarita", Categoria.PIZZA, 9990, Tamano.MEDIANA), 2)

        val resumen = ResumenPedido.generar(pedido)

        assertContains(resumen, "Pedido #7  (Pendiente)")
        assertContains(resumen, "Cliente: Ana")
        assertContains(resumen, "25-10-2026 20:30")
        assertContains(resumen, "2 x Margarita Mediana")
        assertContains(resumen, "$19.980")
        assertContains(resumen, "TOTAL")
    }
}
