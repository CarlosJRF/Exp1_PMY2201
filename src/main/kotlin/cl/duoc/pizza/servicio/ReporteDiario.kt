package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.util.Formato
import java.time.LocalDate

/** Reporte de los pedidos del día: detalle de cada pedido y agrupación en completados y pendientes. */
object ReporteDiario {

    /** Listos o en camino: ya salieron de cocina. */
    fun completados(pedidos: Array<Pedido>): List<Pedido> =
        pedidos.filter { it.estado == EstadoPedido.LISTO || it.estado == EstadoPedido.EN_CAMINO }

    /** Todavía en preparación. */
    fun pendientes(pedidos: Array<Pedido>): List<Pedido> =
        pedidos.filter { it.estado == EstadoPedido.EN_PREPARACION }

    fun generar(pedidos: Array<Pedido>, fecha: LocalDate = LocalDate.now()): String = buildString {
        appendLine("Reporte del día ${Formato.dia(fecha)}: ${pedidos.size} pedido(s)")
        for (pedido in pedidos) {
            val pizzas: Map<Producto, Int> = pedido.cantidades.filterKeys { it.categoria == Categoria.PIZZA }
            val detallePizzas: String =
                if (pizzas.isEmpty()) "(sin pizzas)" else pizzas.entries.joinToString { "${it.value} x ${it.key.nombre}" }
            appendLine("#${pedido.id}  Cliente: ${pedido.cliente.nombre}")
            appendLine("    Pizzas: $detallePizzas")
            appendLine("    Total: ${Formato.precio(pedido.total)}  Estado: ${pedido.estadoVisible}")
        }
        val completados: List<Pedido> = completados(pedidos)
        val pendientes: List<Pedido> = pendientes(pedidos)
        appendLine("Completados (${completados.size}): ${numeros(completados)}")
        append("Pendientes (${pendientes.size}): ${numeros(pendientes)}")
    }

    private fun numeros(pedidos: List<Pedido>): String =
        if (pedidos.isEmpty()) "-" else pedidos.joinToString { "#${it.id}" }
}
