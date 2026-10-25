package cl.duoc.pizza.ui

import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.servicio.PedidoServicio

class MenuAdmin(
    private val consola: Consola,
    private val inventario: InventarioServicio,
    private val pedidoServicio: PedidoServicio,
) {

    companion object {
        private const val REPOSICION_MAXIMA = 1000
    }

    private val opciones = listOf("Ver inventario", "Reponer stock", "Ver todos los pedidos")

    fun mostrar() {
        while (true) {
            val opcion = consola.menu("Menú administrador", opciones, "Cerrar sesión")
            consola.intentar {
                when (opcion) {
                    1 -> verInventario()
                    2 -> reponerStock()
                    3 -> verPedidos()
                }
            }
            if (opcion == 0) return
        }
    }

    private fun verInventario() {
        consola.titulo("Inventario")
        consola.mensaje(Vistas.catalogo(inventario))
        val stockBajo = inventario.productosConStockBajo()
        if (stockBajo.isNotEmpty()) {
            consola.mensaje("")
            consola.error("Stock bajo: ${stockBajo.joinToString { it.nombreCompleto }}")
        }
    }

    private fun reponerStock() {
        verInventario()
        val id = consola.leerEntero("Id del producto (0 para volver)", 0, Int.MAX_VALUE)
        if (id == 0) return
        val producto = inventario.buscarProducto(id)
        val cantidad = consola.leerEntero("Unidades a agregar", 1, REPOSICION_MAXIMA)

        inventario.reponer(producto, cantidad)
        consola.exito("Stock de ${producto.nombreCompleto}: ${inventario.stockDe(producto)} unidades")
    }

    private fun verPedidos() {
        consola.titulo("Todos los pedidos")
        val pedidos = pedidoServicio.todosLosPedidos()
        if (pedidos.isEmpty()) {
            consola.mensaje("No hay pedidos")
            return
        }
        pedidos.forEach { consola.mensaje(Vistas.lineaPedido(it)) }
    }
}
