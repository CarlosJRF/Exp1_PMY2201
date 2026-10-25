package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.util.Formato

object ResumenPedido {
    private const val ANCHO = 44

    /** Genera el texto del resumen; quien lo llama decide dónde mostrarlo. */
    fun generar(pedido: Pedido): String = buildString {
        val linea = "-".repeat(ANCHO)
        appendLine(linea)
        appendLine("Pedido #${pedido.id}  (${pedido.estado.nombreVisible})")
        appendLine("Cliente: ${pedido.usuario.nombre}")
        appendLine("Fecha:   ${Formato.fecha(pedido.fecha)}")
        appendLine(linea)
        if (pedido.estaVacio) {
            appendLine("(sin productos)")
        } else {
            pedido.items.forEach { item ->
                val detalle = "${item.cantidad} x ${item.producto.nombreCompleto}"
                appendLine(alinear(detalle, Formato.precio(item.subtotal)))
            }
        }
        appendLine(linea)
        append(alinear("TOTAL", Formato.precio(pedido.total)))
    }

    /** Texto a la izquierda y monto a la derecha dentro del ancho del resumen. */
    private fun alinear(izquierda: String, derecha: String): String =
        izquierda + " ".repeat(maxOf(1, ANCHO - izquierda.length - derecha.length)) + derecha
}
