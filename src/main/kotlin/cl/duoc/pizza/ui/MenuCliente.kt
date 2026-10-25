package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.servicio.ErrorNegocio
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.servicio.PedidoServicio
import cl.duoc.pizza.servicio.ResumenPedido
import cl.duoc.pizza.util.Formato

class MenuCliente(
    private val usuario: Usuario,
    private val consola: Consola,
    private val inventario: InventarioServicio,
    private val pedidoServicio: PedidoServicio,
) {

    companion object {
        private const val CANTIDAD_MAXIMA = 50
    }

    private val opciones = listOf(
        "Ver catálogo",
        "Agregar producto al pedido",
        "Quitar producto del pedido",
        "Ver resumen del pedido actual",
        "Confirmar pedido",
        "Ver mis pedidos",
        "Cancelar un pedido",
    )

    fun mostrar() {
        while (true) {
            val opcion = consola.menu("Menú cliente — ${usuario.nombre}", opciones, "Cerrar sesión")
            consola.intentar {
                when (opcion) {
                    1 -> verCatalogo()
                    2 -> agregarProducto()
                    3 -> quitarProducto()
                    4 -> verPedidoActual()
                    5 -> confirmarPedido()
                    6 -> verMisPedidos()
                    7 -> cancelarPedido()
                }
            }
            if (opcion == 0) return
        }
    }

    private fun verCatalogo() {
        consola.titulo("Catálogo")
        consola.mensaje(Vistas.catalogo(inventario))
    }

    private fun agregarProducto() {
        verCatalogo()
        val id = consola.leerEntero("Id del producto (0 para volver)", 0, Int.MAX_VALUE)
        if (id == 0) return
        val producto = inventario.buscarProducto(id)
        val cantidad = consola.leerEntero("Cantidad", 1, CANTIDAD_MAXIMA)

        val pedido = pedidoServicio.pedidoActual(usuario)
        pedidoServicio.agregarProducto(pedido, producto, cantidad)
        consola.exito("Agregado: $cantidad x ${producto.nombreCompleto}. Total: ${Formato.precio(pedido.total)}")
    }

    private fun quitarProducto() {
        val pedido = pedidoServicio.pedidoActual(usuario)
        if (pedido.estaVacio) throw ErrorNegocio("Tu pedido está vacío")

        consola.titulo("Quitar producto")
        val items = pedido.items
        items.forEachIndexed { indice, item ->
            consola.mensaje("${indice + 1}. ${item.cantidad} x ${item.producto.nombreCompleto}")
        }
        val numero = consola.leerEntero("Producto a quitar (0 para volver)", 0, items.size)
        if (numero == 0) return
        val item = items[numero - 1]
        val cantidad = consola.leerEntero("Cantidad a quitar", 1, item.cantidad)

        pedidoServicio.quitarProducto(pedido, item.producto, cantidad)
        consola.exito("Quitado: $cantidad x ${item.producto.nombreCompleto}. Total: ${Formato.precio(pedido.total)}")
    }

    private fun verPedidoActual() {
        consola.titulo("Pedido actual")
        consola.mensaje(ResumenPedido.generar(pedidoServicio.pedidoActual(usuario)))
    }

    private fun confirmarPedido() {
        val pedido = pedidoServicio.pedidoActual(usuario)
        if (pedido.estaVacio) throw ErrorNegocio("Tu pedido está vacío")

        verPedidoActual()
        if (consola.confirmar("¿Confirmar el pedido?")) {
            pedidoServicio.confirmar(pedido)
            consola.exito("Pedido #${pedido.id} confirmado. ¡Gracias por tu compra!")
        }
    }

    private fun verMisPedidos() {
        consola.titulo("Mis pedidos")
        val pedidos = pedidoServicio.pedidosDe(usuario).filter { !it.estaVacio }
        if (pedidos.isEmpty()) {
            consola.mensaje("Aún no tienes pedidos")
            return
        }
        pedidos.forEach { consola.mensaje(ResumenPedido.generar(it)) }
    }

    private fun cancelarPedido() {
        val cancelables = pedidoServicio.pedidosDe(usuario)
            .filter { !it.estaVacio && it.estado != EstadoPedido.CANCELADO }
        if (cancelables.isEmpty()) throw ErrorNegocio("No tienes pedidos para cancelar")

        consola.titulo("Cancelar pedido")
        cancelables.forEach { consola.mensaje(Vistas.lineaPedido(it)) }
        val id = consola.leerEntero("Número de pedido (0 para volver)", 0, Int.MAX_VALUE)
        if (id == 0) return
        val pedido = cancelables.find { it.id == id } ?: throw ErrorNegocio("No tienes un pedido #$id para cancelar")

        if (consola.confirmar("¿Cancelar el pedido #${pedido.id}?")) {
            pedidoServicio.cancelar(pedido)
            consola.exito("Pedido #${pedido.id} cancelado")
        }
    }
}
