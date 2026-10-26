package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Descuento
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.util.Formato

object ResumenPedido {
    private const val ANCHO = 52

    /**
     * Genera el texto del resumen con tres bloques: datos del pedido, datos del cliente
     * y detalle de productos con subtotal, descuento y total. Quien lo llama decide dónde mostrarlo.
     */
    fun generar(pedido: Pedido): String = buildString {
        val linea: String = "-".repeat(ANCHO)
        appendLine(linea)
        appendLine("Pedido #${pedido.id}  (${pedido.estado.nombreVisible})")
        appendLine("Fecha:     ${Formato.fecha(pedido.fecha)}")
        appendLine(linea)
        appendLine("CLIENTE")
        appendLine("Nombre:    ${pedido.usuario.nombre}")
        appendLine("Email:     ${pedido.usuario.email}")
        appendLine("Teléfono:  ${pedido.usuario.telefono.ifBlank { "-" }}")
        appendLine("Dirección: ${pedido.usuario.direccion.ifBlank { "-" }}")
        appendLine(linea)
        appendLine("DETALLE")
        if (pedido.estaVacio) {
            appendLine("(sin productos)")
        } else {
            pedido.items.forEach { item ->
                val detalle: String =
                    "${item.cantidad} x ${item.producto.nombreCompleto} (${Formato.precio(item.producto.precio)} c/u)"
                appendLine(alinear(detalle, Formato.precio(item.subtotal)))
            }
        }
        appendLine(linea)
        appendLine("Unidades: ${pedido.cantidadUnidades}")
        appendLine(alinear("Subtotal", Formato.precio(pedido.subtotal)))
        val descuento: Descuento? = pedido.descuento
        if (descuento != null) {
            appendLine(alinear("Descuento ${descuento.porcentaje}% (${descuento.descripcion})", Formato.precio(-descuento.monto)))
        }
        append(alinear("TOTAL", Formato.precio(pedido.total)))
    }

    /** Texto a la izquierda y monto a la derecha dentro del ancho del resumen. */
    private fun alinear(izquierda: String, derecha: String): String =
        izquierda + " ".repeat(maxOf(1, ANCHO - izquierda.length - derecha.length)) + derecha
}
