package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Cliente
import cl.duoc.pizza.repositorio.PedidoRepositorio
import cl.duoc.pizza.repositorio.ProductoRepositorio
import cl.duoc.pizza.repositorio.StockRepositorio
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import cl.duoc.pizza.modelo.Pedido
import kotlin.test.assertSame

class PedidoServicioTest {
    private val usuario = Cliente(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123")
    private val pizza = Producto(1, "Margarita", Categoria.PIZZA, 9990, ingredientes = mapOf("Masa" to 1, "Queso" to 1))
    private val bebida = Producto(2, "Agua", Categoria.BEBIDA, 1290)
    private val inventario = InventarioServicio(
        ProductoRepositorio(listOf(pizza, bebida)),
        StockRepositorio(mapOf("Masa" to 10, "Queso" to 3, "Agua" to 10)),
    )
    private val servicio = PedidoServicio(PedidoRepositorio(), inventario)

    @Test
    fun `el carrito se reutiliza mientras no se confirme`() {
        val pedido = servicio.pedidoActual(usuario)
        assertSame(pedido, servicio.pedidoActual(usuario))
        assertNull(pedido.estado)

        servicio.agregarProducto(pedido, pizza, 1)
        servicio.confirmar(pedido)
        assertNotEquals(pedido.id, servicio.pedidoActual(usuario).id)
    }

    @Test
    fun `calcula el total del pedido`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 2)
        servicio.agregarProducto(pedido, bebida, 3)
        assertEquals(2 * 9990 + 3 * 1290, pedido.subtotal)
        assertEquals(23850 + 4532, pedido.total)
    }

    @Test
    fun `no acepta productos si faltan ingredientes`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 2)
        assertEquals(1, servicio.disponibleParaAgregar(pedido, pizza))

        val error = assertFailsWith<ErrorNegocio> { servicio.agregarProducto(pedido, pizza, 2) }
        assertContains(error.message!!, "No se puede agregar 2 x Margarita")
        assertContains(error.message!!, "Queso: hay 3, se necesitan 4 (faltan 1)")
        assertContains(error.message!!, "Pasos para reabastecer")
        assertEquals(2, pedido.cantidadDe(pizza))
    }

    @Test
    fun `confirmar descuenta los ingredientes y deja el pedido en preparacion`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 2)
        servicio.confirmar(pedido)

        assertEquals(EstadoPedido.EN_PREPARACION, pedido.estado)
        assertEquals(1, inventario.stockDe("Queso"))
        assertEquals(8, inventario.stockDe("Masa"))
        assertFailsWith<ErrorNegocio> { servicio.agregarProducto(pedido, bebida, 1) }
    }

    @Test
    fun `las notas en blanco quedan como null`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 1)
        servicio.confirmar(pedido, "   ")
        assertNull(pedido.notas)

        val otro = servicio.pedidoActual(usuario)
        servicio.agregarProducto(otro, bebida, 1)
        servicio.confirmar(otro, "  Sin hielo ")
        assertEquals("Sin hielo", otro.notas)
    }

    @Test
    fun `no confirma un pedido vacio`() {
        assertFailsWith<ErrorNegocio> { servicio.confirmar(servicio.pedidoActual(usuario)) }
    }

    @Test
    fun `no confirma si otro pedido se llevo los ingredientes`() {
        val otro = Cliente(2, "Luis", "luis@duoc.cl", "hash", "987654321", "Los Aromos 45")
        val pedidoAna = servicio.pedidoActual(usuario)
        val pedidoLuis = servicio.pedidoActual(otro)
        servicio.agregarProducto(pedidoAna, pizza, 2)
        servicio.agregarProducto(pedidoLuis, pizza, 2)
        servicio.agregarProducto(pedidoLuis, bebida, 1)

        servicio.confirmar(pedidoAna)
        val error = assertFailsWith<ErrorNegocio> { servicio.confirmar(pedidoLuis) }
        assertContains(error.message!!, "Queso: hay 1, se necesitan 2 (faltan 1)")
        assertNull(pedidoLuis.estado)
        assertEquals(10, inventario.stockDe("Agua"))
    }

    @Test
    fun `un pedido en preparacion se marca como listo`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 1)
        assertFailsWith<ErrorNegocio> { servicio.marcarListo(pedido) }

        servicio.confirmar(pedido)
        servicio.marcarListo(pedido)
        assertEquals(EstadoPedido.LISTO, pedido.estado)
        assertFailsWith<ErrorNegocio> { servicio.marcarListo(pedido) }
    }

    @Test
    fun `cancelar un pedido en preparacion devuelve los ingredientes`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 3)
        servicio.confirmar(pedido)
        assertEquals(0, inventario.stockDe("Queso"))

        servicio.cancelar(pedido)
        assertEquals(3, inventario.stockDe("Queso"))
        assertFailsWith<ErrorNegocio> { servicio.buscarPedido(pedido.id) }
    }

    @Test
    fun `no se cancela un pedido listo`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 1)
        servicio.confirmar(pedido)
        servicio.marcarListo(pedido)
        assertFailsWith<ErrorNegocio> { servicio.cancelar(pedido) }
    }

    @Test
    fun `pedidos del dia es un arreglo solo con pedidos confirmados de hoy`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 1)
        servicio.confirmar(pedido)
        servicio.pedidoActual(usuario)

        val hoy: Array<Pedido> = servicio.pedidosDelDia()
        assertEquals(listOf(pedido.id), hoy.map { it.id })
        assertEquals(0, servicio.pedidosDelDia(LocalDate.now().minusDays(1)).size)
    }

    @Test
    fun `quitar productos del pedido`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 2)
        servicio.quitarProducto(pedido, pizza, 1)
        assertEquals(1, pedido.cantidadDe(pizza))
        assertFailsWith<ErrorNegocio> { servicio.quitarProducto(pedido, bebida, 1) }
    }
}
