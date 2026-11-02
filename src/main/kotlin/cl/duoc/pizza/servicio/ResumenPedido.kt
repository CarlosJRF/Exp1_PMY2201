package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.PIZZAS_PARA_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_IVA
import cl.duoc.pizza.modelo.Totales
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.util.Formato

object ResumenPedido {
    private const val ANCHO = 52

    /**
     * Genera el texto del resumen con tres bloques: datos del pedido, datos del cliente
     * y detalle de productos con subtotal, descuento, IVA y total. Quien lo llama decide dónde mostrarlo.
     */
    fun generar(pedido: Pedido): String = buildString {
        val linea: String = "-".repeat(ANCHO)
        appendLine(linea)
        appendLine("Pedido #${pedido.id}  (${pedido.estadoVisible})")
        appendLine("Fecha:     ${Formato.fecha(pedido.fecha)}")
        appendLine(linea)
        appendLine("CLIENTE")
        appendLine("Nombre:    ${pedido.cliente.nombre}")
        appendLine("Email:     ${pedido.cliente.email}")
        appendLine("Teléfono:  ${pedido.cliente.telefono.ifBlank { "-" }}")
        appendLine("Dirección: ${pedido.cliente.direccion.ifBlank { "-" }}")
        appendLine(linea)
        appendLine("DETALLE")
        if (pedido.estaVacio) {
            appendLine("(sin productos)")
        } else {
            for ((producto, cantidad) in pedido.cantidades) {
                val detalle: String = "$cantidad x ${producto.nombreCompleto} (${Formato.precio(producto.precio)} c/u)"
                appendLine(alinear(detalle, Formato.precio(producto.precio * cantidad)))
            }
        }
        appendLine(linea)
        appendLine("Unidades: ${pedido.cantidadUnidades}")
        val totales: Totales = pedido.calcularTotal()
        appendLine(alinear("Subtotal", Formato.precio(totales.subtotal)))
        if (totales.descuento > 0) {
            appendLine(alinear("Descuento $PORCENTAJE_DESCUENTO% (más de $PIZZAS_PARA_DESCUENTO pizzas)", Formato.precio(-totales.descuento)))
        }
        appendLine(alinear("IVA $PORCENTAJE_IVA%", Formato.precio(totales.impuesto)))
        appendLine(alinear("TOTAL", Formato.precio(totales.total)))
        appendLine(linea)
        append("Notas:     ${pedido.notas ?: "Sin notas"}")
    }

    /** Texto a la izquierda y monto a la derecha dentro del ancho del resumen. */
    private fun alinear(izquierda: String, derecha: String): String =
        izquierda + " ".repeat(maxOf(1, ANCHO - izquierda.length - derecha.length)) + derecha
}
