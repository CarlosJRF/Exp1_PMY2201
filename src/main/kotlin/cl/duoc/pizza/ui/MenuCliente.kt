package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Cliente
import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Totales
import cl.duoc.pizza.servicio.ErrorNegocio
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.servicio.Notificador
import cl.duoc.pizza.servicio.PedidoServicio
import cl.duoc.pizza.servicio.ResumenPedido
import cl.duoc.pizza.util.Formato

class MenuCliente(
    private val cliente: Cliente,
    private val consola: Consola,
    private val inventario: InventarioServicio,
    private val pedidoServicio: PedidoServicio,
) {

    companion object {
        private const val CANTIDAD_MAXIMA = 50
    }

    private val opciones: List<String> = listOf(
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
            val opcion: Int = consola.menu("Menú cliente — ${cliente.nombre}", opciones, "Cerrar sesión")
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
        consola.mensaje("")
        consola.mensaje(Vistas.promociones())
    }

    private fun agregarProducto() {
        verCatalogo()
        val id: Int = consola.leerEntero("Id del producto (0 para volver)", 0, Int.MAX_VALUE)
        if (id == 0) return
        val producto: Producto = inventario.buscarProducto(id)
        consola.mensaje(producto.detallarProducto())
        val pedido: Pedido = pedidoServicio.pedidoActual(cliente)
        pedidoServicio.verificarDisponibilidad(pedido, producto, 1)
        val disponible: Int = pedidoServicio.disponibleParaAgregar(pedido, producto)
        consola.mensaje("Disponible para agregar: $disponible")
        val cantidad: Int = consola.leerEntero("Cantidad", 1, minOf(CANTIDAD_MAXIMA, disponible))

        pedidoServicio.agregarProducto(pedido, producto, cantidad)
        consola.exito("Agregado: $cantidad x ${producto.nombreCompleto}. ${totalConDescuento(pedido)}")
        if (inventario.tieneStockBajo(producto)) {
            consola.mensaje("Aviso: ${producto.nombreCompleto} tiene poco stock (${inventario.disponibilidad(producto)})")
        }
    }

    private fun quitarProducto() {
        val pedido: Pedido = pedidoServicio.pedidoActual(cliente)
        if (pedido.estaVacio) throw ErrorNegocio("Tu pedido está vacío")

        consola.titulo("Quitar producto")
        val enPedido: List<Producto> = pedido.cantidades.keys.toList()
        enPedido.forEachIndexed { indice, producto ->
            consola.mensaje("${indice + 1}. ${pedido.cantidadDe(producto)} x ${producto.nombreCompleto}")
        }
        val numero: Int = consola.leerEntero("Producto a quitar (0 para volver)", 0, enPedido.size)
        if (numero == 0) return
        val producto: Producto = enPedido[numero - 1]
        val cantidad: Int = consola.leerEntero("Cantidad a quitar", 1, pedido.cantidadDe(producto))

        pedidoServicio.quitarProducto(pedido, producto, cantidad)
        consola.exito("Quitado: $cantidad x ${producto.nombreCompleto}. ${totalConDescuento(pedido)}")
    }

    /** "Total: $X" (con IVA) y, si corresponde, el descuento aplicado. */
    private fun totalConDescuento(pedido: Pedido): String {
        val totales: Totales = pedido.calcularTotal()
        val total: String = "Total con IVA: ${Formato.precio(totales.total)}"
        return if (totales.descuento == 0) total else "$total (incluye ${Formato.precio(totales.descuento)} de descuento)"
    }

    private fun verPedidoActual() {
        consola.titulo("Pedido actual")
        consola.mensaje(ResumenPedido.generar(pedidoServicio.pedidoActual(cliente)))
    }

    private fun confirmarPedido() {
        val pedido: Pedido = pedidoServicio.pedidoActual(cliente)
        if (pedido.estaVacio) throw ErrorNegocio("Tu pedido está vacío")

        verPedidoActual()
        if (!consola.confirmar("¿Confirmar el pedido?")) return
        val notas: String? = consola.leerOpcional("Notas para el pedido (Enter para omitir)")
        try {
            pedidoServicio.confirmar(pedido, notas)
        } catch (e: ErrorNegocio) {
            consola.error(e.message ?: "No se pudo confirmar el pedido")
            consola.mensaje("Corrige tu pedido (opciones 2 y 3) y vuelve a confirmarlo.")
            return
        }
        consola.exito("Pedido #${pedido.id} confirmado. ¡Gracias por tu compra!")
        consola.mensaje(ResumenPedido.generar(pedido))
        Notificador.notificar(pedido)?.let { consola.mensaje(it) }
    }

    private fun verMisPedidos() {
        consola.titulo("Mis pedidos")
        val pedidos: List<Pedido> = pedidoServicio.pedidosDe(cliente).filter { !it.estaVacio }
        if (pedidos.isEmpty()) {
            consola.mensaje("Aún no tienes pedidos")
            return
        }
        pedidos.forEach { consola.mensaje(ResumenPedido.generar(it)) }
    }

    private fun cancelarPedido() {
        val cancelables: List<Pedido> = pedidoServicio.pedidosDe(cliente)
            .filter { (it.estado == null && !it.estaVacio) || it.estado == EstadoPedido.EN_PREPARACION }
        if (cancelables.isEmpty()) throw ErrorNegocio("No tienes pedidos para cancelar")

        consola.titulo("Cancelar pedido")
        cancelables.forEach { consola.mensaje(Vistas.lineaPedido(it)) }
        val id: Int = consola.leerEntero("Número de pedido (0 para volver)", 0, Int.MAX_VALUE)
        if (id == 0) return
        val pedido: Pedido = cancelables.find { it.id == id } ?: throw ErrorNegocio("No tienes un pedido #$id para cancelar")

        if (consola.confirmar("¿Cancelar el pedido #${pedido.id}?")) {
            pedidoServicio.cancelar(pedido)
            consola.exito("Pedido #${pedido.id} cancelado")
        }
    }
}
