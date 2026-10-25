package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.repositorio.PedidoRepositorio
import cl.duoc.pizza.repositorio.ProductoRepositorio
import cl.duoc.pizza.repositorio.StockRepositorio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class PedidoServicioTest {
    private val usuario = Usuario(1, "Ana", "ana@duoc.cl", "hash", "912345678", "Av. Siempre Viva 123")
    private val pizza = Producto(1, "Margarita", Categoria.PIZZA, 9990)
    private val bebida = Producto(2, "Agua", Categoria.BEBIDA, 1290)
    private val inventario = InventarioServicio(
        ProductoRepositorio(listOf(pizza, bebida)),
        StockRepositorio(mapOf(pizza.id to 3, bebida.id to 10)),
    )
    private val servicio = PedidoServicio(PedidoRepositorio(), inventario)

    @Test
    fun `el pedido actual se reutiliza mientras este pendiente`() {
        val pedido = servicio.pedidoActual(usuario)
        assertSame(pedido, servicio.pedidoActual(usuario))

        servicio.agregarProducto(pedido, pizza, 1)
        servicio.confirmar(pedido)
        assertNotEquals(pedido.id, servicio.pedidoActual(usuario).id)
    }

    @Test
    fun `calcula el total del pedido`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 2)
        servicio.agregarProducto(pedido, bebida, 3)
        assertEquals(2 * 9990 + 3 * 1290, pedido.total)
    }

    @Test
    fun `no permite agregar mas que el stock disponible`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 2)
        assertFailsWith<ErrorNegocio> { servicio.agregarProducto(pedido, pizza, 2) }
        assertEquals(2, pedido.cantidadDe(pizza))
    }

    @Test
    fun `confirmar descuenta el stock`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 2)
        servicio.confirmar(pedido)

        assertEquals(EstadoPedido.CONFIRMADO, pedido.estado)
        assertEquals(1, inventario.stockDe(pizza))
        assertFailsWith<ErrorNegocio> { servicio.agregarProducto(pedido, bebida, 1) }
    }

    @Test
    fun `no confirma un pedido vacio`() {
        assertFailsWith<ErrorNegocio> { servicio.confirmar(servicio.pedidoActual(usuario)) }
    }

    @Test
    fun `no confirma si otro pedido se llevo el stock`() {
        val otro = usuario.copy(id = 2, email = "luis@duoc.cl")
        val pedidoAna = servicio.pedidoActual(usuario)
        val pedidoLuis = servicio.pedidoActual(otro)
        servicio.agregarProducto(pedidoAna, pizza, 2)
        servicio.agregarProducto(pedidoLuis, pizza, 2)
        servicio.agregarProducto(pedidoLuis, bebida, 1)

        servicio.confirmar(pedidoAna)
        assertFailsWith<ErrorNegocio> { servicio.confirmar(pedidoLuis) }
        assertEquals(EstadoPedido.PENDIENTE, pedidoLuis.estado)
        assertEquals(10, inventario.stockDe(bebida))
    }

    @Test
    fun `cancelar un pedido confirmado devuelve el stock`() {
        val pedido = servicio.pedidoActual(usuario)
        servicio.agregarProducto(pedido, pizza, 3)
        servicio.confirmar(pedido)
        assertEquals(0, inventario.stockDe(pizza))

        servicio.cancelar(pedido)
        assertEquals(EstadoPedido.CANCELADO, pedido.estado)
        assertEquals(3, inventario.stockDe(pizza))
        assertFailsWith<ErrorNegocio> { servicio.cancelar(pedido) }
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
