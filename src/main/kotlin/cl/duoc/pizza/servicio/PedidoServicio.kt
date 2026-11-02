package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.ItemPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.repositorio.PedidoRepositorio
import java.time.LocalDate

class PedidoServicio(
    private val pedidoRepositorio: PedidoRepositorio,
    private val inventario: InventarioServicio,
) {

    /** Devuelve el carrito (pedido sin confirmar) del usuario, o crea uno nuevo si no tiene. */
    fun pedidoActual(usuario: Usuario): Pedido =
        pedidosDe(usuario).find { it.estado == null }
            ?: Pedido(pedidoRepositorio.siguienteId(), usuario).also { pedidoRepositorio.guardar(it) }

    /** Unidades que todavía se pueden agregar: lo que alcanza con los ingredientes que no usa ya el pedido. */
    fun disponibleParaAgregar(pedido: Pedido, producto: Producto): Int =
        inventario.unidadesDisponibles(producto, inventario.necesarios(pedido.items))

    /** Lanza ErrorNegocio con los ingredientes insuficientes si no se pueden agregar [cantidad] unidades. */
    fun verificarDisponibilidad(pedido: Pedido, producto: Producto, cantidad: Int) {
        if (cantidad <= disponibleParaAgregar(pedido, producto)) return
        val faltantes: List<IngredienteFaltante> =
            inventario.evaluarDisponibilidad(pedido.items + ItemPedido(producto, cantidad))
        throw ErrorNegocio("No se puede agregar $cantidad x ${producto.nombreCompleto}.\n${inventario.mensajeFaltantes(faltantes)}")
    }

    fun agregarProducto(pedido: Pedido, producto: Producto, cantidad: Int) {
        validarCarrito(pedido)
        if (cantidad <= 0) throw ErrorNegocio("La cantidad debe ser mayor a 0")
        verificarDisponibilidad(pedido, producto, cantidad)
        pedido.agregar(producto, cantidad)
    }

    fun quitarProducto(pedido: Pedido, producto: Producto, cantidad: Int) {
        validarCarrito(pedido)
        if (cantidad <= 0) throw ErrorNegocio("La cantidad debe ser mayor a 0")
        if (pedido.cantidadDe(producto) == 0) throw ErrorNegocio("El producto no está en el pedido")
        pedido.quitar(producto, cantidad)
    }

    /** Evalúa los ingredientes, los descuenta y deja el pedido en preparación. Si falta alguno, no descuenta nada. */
    fun confirmar(pedido: Pedido) {
        validarCarrito(pedido)
        if (pedido.estaVacio) throw ErrorNegocio("No se puede confirmar un pedido vacío")

        val faltantes: List<IngredienteFaltante> = inventario.evaluarDisponibilidad(pedido.items)
        if (faltantes.isNotEmpty()) {
            throw ErrorNegocio("No se pudo confirmar el pedido #${pedido.id}.\n${inventario.mensajeFaltantes(faltantes)}")
        }

        inventario.descontar(pedido.items)
        pedido.estado = EstadoPedido.EN_PREPARACION
    }

    /** Pasa el pedido al siguiente estado: En preparación -> Listo -> En camino. */
    fun avanzarEstado(pedido: Pedido) {
        pedido.estado = when (pedido.estado) {
            null -> throw ErrorNegocio("El pedido #${pedido.id} aún no está confirmado")
            EstadoPedido.EN_PREPARACION -> EstadoPedido.LISTO
            EstadoPedido.LISTO -> EstadoPedido.EN_CAMINO
            EstadoPedido.EN_CAMINO -> throw ErrorNegocio("El pedido #${pedido.id} ya está en camino")
        }
    }

    /** Elimina el pedido; si estaba en preparación, devuelve los ingredientes al inventario. */
    fun cancelar(pedido: Pedido) {
        when (pedido.estado) {
            null -> Unit
            EstadoPedido.EN_PREPARACION -> inventario.devolver(pedido.items)
            EstadoPedido.LISTO, EstadoPedido.EN_CAMINO ->
                throw ErrorNegocio("El pedido #${pedido.id} ya está ${pedido.estadoVisible.lowercase()} y no se puede cancelar")
        }
        pedidoRepositorio.eliminar(pedido.id)
    }

    fun buscarPedido(id: Int): Pedido =
        pedidoRepositorio.buscarPorId(id) ?: throw ErrorNegocio("No existe un pedido con id $id")

    fun pedidosDe(usuario: Usuario): List<Pedido> = pedidoRepositorio.listarPorUsuario(usuario.id)

    /** Todos los pedidos confirmados (sin carritos). */
    fun todosLosPedidos(): List<Pedido> = pedidoRepositorio.listar().filter { it.estado != null }

    /** Pedidos confirmados en la fecha indicada, como arreglo para el reporte diario. */
    fun pedidosDelDia(fecha: LocalDate = LocalDate.now()): Array<Pedido> =
        todosLosPedidos().filter { it.fecha.toLocalDate() == fecha }.toTypedArray()

    private fun validarCarrito(pedido: Pedido) {
        if (pedido.estado != null) {
            throw ErrorNegocio("El pedido #${pedido.id} ya está confirmado (${pedido.estadoVisible.lowercase()})")
        }
    }
}
