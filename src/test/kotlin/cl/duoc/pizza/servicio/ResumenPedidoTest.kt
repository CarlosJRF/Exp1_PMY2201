package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Tamano
import cl.duoc.pizza.modelo.Cliente
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ResumenPedidoTest {
    private val usuario = Cliente(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123")
    private val margarita = Producto(1, "Margarita", Categoria.PIZZA, 9990, Tamano.MEDIANA)

    @Test
    fun `el resumen incluye datos del pedido, del cliente y del producto`() {
        val pedido = Pedido(7, usuario, LocalDateTime.of(2026, 10, 25, 20, 30))
        pedido.agregar(margarita, 2)

        val resumen = ResumenPedido.generar(pedido)

        assertContains(resumen, "DuocPizza · Pedido #7")
        assertContains(resumen, "Fecha:     25-10-2026 20:30")
        assertContains(resumen, "Estado:    En carrito")
        assertContains(resumen, "Nombre:    Ana")
        assertContains(resumen, "Email:     ana@duoc.cl")
        assertContains(resumen, "Teléfono:  912345678")
        assertContains(resumen, "Dirección: Av. Siempre Viva 123")
        assertContains(resumen, "Producto                   Precio Cant.     Subtotal")
        assertContains(resumen, "Margarita Mediana          $9.990     2      $19.980")
        assertContains(resumen, "Unidades: 2")
        assertContains(resumen, "$19.980")
        assertContains(resumen, "IVA 19%")
        assertContains(resumen, "$3.796")
        assertContains(resumen, "$23.776")
        assertContains(resumen, "Descuento (no aplica)")
        assertContains(resumen, "Notas:     Sin notas")
    }

    @Test
    fun `el resumen muestra el descuento aplicado`() {
        val pedido = Pedido(8, usuario)
        pedido.agregar(margarita, 4)

        val resumen = ResumenPedido.generar(pedido)

        assertContains(resumen, "$39.960")
        assertContains(resumen, "Descuento 10% (más de 3 pizzas)")
        assertContains(resumen, "-$3.996")
        assertContains(resumen, "$6.833")
        assertContains(resumen, "$42.797")
    }

    @Test
    fun `el resumen muestra el estado del pedido confirmado`() {
        val pedido = Pedido(10, usuario)
        pedido.agregar(margarita, 1)
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION)
        assertContains(ResumenPedido.generar(pedido), "Estado:    En preparación")

        pedido.cambiarEstado(EstadoPedido.LISTO)
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        assertContains(ResumenPedido.generar(pedido), "Estado:    En camino")
    }

    @Test
    fun `todas las lineas caben en el ancho del resumen`() {
        val pedido = Pedido(11, usuario)
        pedido.agregar(margarita, 12)
        pedido.agregar(Producto(2, "Napolitana", Categoria.PIZZA, 14990, Tamano.FAMILIAR), 2)

        val anchoMaximo: Int = ResumenPedido.generar(pedido).lines().maxOf { it.length }
        assertEquals(52, anchoMaximo)
    }

    @Test
    fun `el resumen muestra las notas del cliente`() {
        val pedido = Pedido(9, usuario)
        pedido.agregar(margarita, 1)
        pedido.notas = "Sin cebolla, tocar timbre"

        assertContains(ResumenPedido.generar(pedido), "Notas:     Sin cebolla, tocar timbre")
    }
}
