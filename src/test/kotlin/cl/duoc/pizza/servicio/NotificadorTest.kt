package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Cliente
import cl.duoc.pizza.modelo.ContactoPredeterminado
import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificadorTest {
    private fun pedidoDe(email: String): Pedido =
        Pedido(4, Cliente(1, "Ana", email, "hash", "912345678", "Av. Siempre Viva 123")).apply {
            agregar(Producto(1, "Margarita", Categoria.PIZZA, 9990), 1)
            cambiarEstado(EstadoPedido.EN_PREPARACION)
        }

    @Test
    fun `notifica al cliente con email valido`() {
        assertEquals("✉ Notificación a ana@duoc.cl: tu pedido #4 está en preparación", Notificador.notificar(pedidoDe("ana@duoc.cl")))
    }

    @Test
    fun `no notifica si el cliente quedo con el email predeterminado`() {
        assertNull(Notificador.notificar(pedidoDe(ContactoPredeterminado.EMAIL)))
    }
}
