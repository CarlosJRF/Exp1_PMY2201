package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.repositorio.PedidoRepositorio

class PedidoServicio(
    private val pedidoRepositorio: PedidoRepositorio,
    private val inventario: InventarioServicio,
) {

    /** Devuelve el pedido pendiente (carrito) del usuario, o crea uno nuevo si no tiene. */
    fun pedidoActual(usuario: Usuario): Pedido =
        pedidosDe(usuario).find { it.estado == EstadoPedido.PENDIENTE }
            ?: Pedido(pedidoRepositorio.siguienteId(), usuario).also { pedidoRepositorio.guardar(it) }

    fun agregarProducto(pedido: Pedido, producto: Producto, cantidad: Int) {
        validarPendiente(pedido)
        if (cantidad <= 0) throw ErrorNegocio("La cantidad debe ser mayor a 0")
        val cantidadTotal = pedido.cantidadDe(producto) + cantidad
        if (!inventario.hayStock(producto, cantidadTotal)) {
            throw ErrorNegocio(
                "Stock insuficiente de ${producto.nombreCompleto} (disponible: ${inventario.stockDe(producto)})"
            )
        }
        pedido.agregar(producto, cantidad)
    }

    fun quitarProducto(pedido: Pedido, producto: Producto, cantidad: Int) {
        validarPendiente(pedido)
        if (cantidad <= 0) throw ErrorNegocio("La cantidad debe ser mayor a 0")
        if (pedido.cantidadDe(producto) == 0) throw ErrorNegocio("El producto no está en el pedido")
        pedido.quitar(producto, cantidad)
    }

    /** Confirma el pedido y descuenta el stock. Si falta stock de algún producto, no descuenta nada. */
    fun confirmar(pedido: Pedido) {
        validarPendiente(pedido)
        if (pedido.estaVacio) throw ErrorNegocio("No se puede confirmar un pedido vacío")

        val sinStock = pedido.items.filter { !inventario.hayStock(it.producto, it.cantidad) }
        if (sinStock.isNotEmpty()) {
            val nombres = sinStock.joinToString { it.producto.nombreCompleto }
            throw ErrorNegocio("Stock insuficiente para: $nombres")
        }

        pedido.items.forEach { inventario.descontar(it.producto, it.cantidad) }
        pedido.estado = EstadoPedido.CONFIRMADO
    }

    /** Cancela el pedido; si ya estaba confirmado, devuelve el stock. */
    fun cancelar(pedido: Pedido) {
        when (pedido.estado) {
            EstadoPedido.CANCELADO -> throw ErrorNegocio("El pedido ya está cancelado")
            EstadoPedido.CONFIRMADO -> pedido.items.forEach { inventario.reponer(it.producto, it.cantidad) }
            EstadoPedido.PENDIENTE -> Unit
        }
        pedido.estado = EstadoPedido.CANCELADO
    }

    fun buscarPedido(id: Int): Pedido =
        pedidoRepositorio.buscarPorId(id) ?: throw ErrorNegocio("No existe un pedido con id $id")

    fun pedidosDe(usuario: Usuario): List<Pedido> = pedidoRepositorio.listarPorUsuario(usuario.id)

    /** Todos los pedidos, sin carritos vacíos. */
    fun todosLosPedidos(): List<Pedido> = pedidoRepositorio.listar().filter { !it.estaVacio }

    private fun validarPendiente(pedido: Pedido) {
        if (pedido.estado != EstadoPedido.PENDIENTE) {
            throw ErrorNegocio("El pedido #${pedido.id} ya está ${pedido.estado.nombreVisible.lowercase()}")
        }
    }
}
