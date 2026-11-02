package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Repartidor
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.servicio.PedidoServicio
import cl.duoc.pizza.servicio.RepartoServicio
import cl.duoc.pizza.servicio.ReporteDiario

class MenuAdmin(
    private val consola: Consola,
    private val inventario: InventarioServicio,
    private val pedidoServicio: PedidoServicio,
    private val reparto: RepartoServicio,
) {

    companion object {
        private const val REPOSICION_MAXIMA = 1000
    }

    private val opciones: List<String> = listOf(
        "Ver inventario de ingredientes",
        "Reponer ingrediente",
        "Ver todos los pedidos",
        "Reporte del día",
        "Marcar pedido como listo",
        "Asignar repartidor a un pedido listo",
    )

    fun mostrar() {
        while (true) {
            val opcion: Int = consola.menu("Menú administrador", opciones, "Cerrar sesión")
            consola.intentar {
                when (opcion) {
                    1 -> verInventario()
                    2 -> reponerIngrediente()
                    3 -> verPedidos()
                    4 -> verReporteDelDia()
                    5 -> marcarListo()
                    6 -> asignarRepartidor()
                }
            }
            if (opcion == 0) return
        }
    }

    private fun verInventario() {
        consola.titulo("Inventario de ingredientes")
        consola.mensaje(Vistas.ingredientes(inventario))
        val stockBajo: List<String> = inventario.ingredientesConStockBajo()
        if (stockBajo.isNotEmpty()) {
            consola.mensaje("")
            consola.error("Stock bajo: ${stockBajo.joinToString()}")
        }
    }

    private fun reponerIngrediente() {
        verInventario()
        val nombres: List<String> = inventario.ingredientes().keys.toList()
        val numero: Int = consola.leerEntero("Número del ingrediente (0 para volver)", 0, nombres.size)
        if (numero == 0) return
        val ingrediente: String = nombres[numero - 1]
        val cantidad: Int = consola.leerEntero("Cantidad a agregar", 1, REPOSICION_MAXIMA)

        inventario.reponer(ingrediente, cantidad)
        consola.exito("Stock de $ingrediente: ${inventario.stockDe(ingrediente)}")
    }

    private fun verPedidos() {
        consola.titulo("Todos los pedidos")
        val pedidos: List<Pedido> = pedidoServicio.todosLosPedidos()
        if (pedidos.isEmpty()) {
            consola.mensaje("No hay pedidos")
            return
        }
        pedidos.forEach { consola.mensaje(Vistas.lineaPedido(it)) }
    }

    private fun verReporteDelDia() {
        consola.titulo("Reporte del día")
        consola.mensaje(ReporteDiario.generar(pedidoServicio.pedidosDelDia()))
    }

    private fun marcarListo() {
        val pedido: Pedido = elegirPedido() ?: return
        pedidoServicio.marcarListo(pedido)
        consola.exito("Pedido #${pedido.id}: ${pedido.estadoVisible}")
    }

    private fun asignarRepartidor() {
        consola.titulo("Repartidores")
        reparto.repartidores().forEach { consola.mensaje(Vistas.lineaRepartidor(it)) }
        val pedido: Pedido = elegirPedido() ?: return
        val repartidor: Repartidor = reparto.asignar(pedido)
        consola.exito("Pedido #${pedido.id} asignado a ${repartidor.nombre} (${repartidor.distanciaKm} km): ${pedido.estadoVisible}")
    }

    /** Muestra los pedidos y devuelve el elegido, o null si el usuario vuelve. */
    private fun elegirPedido(): Pedido? {
        verPedidos()
        val id: Int = consola.leerEntero("Número de pedido (0 para volver)", 0, Int.MAX_VALUE)
        return if (id == 0) null else pedidoServicio.buscarPedido(id)
    }
}
