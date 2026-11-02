package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.PIZZAS_PARA_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_IVA
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Totales
import cl.duoc.pizza.util.Formato

object ResumenPedido {
    private const val ANCHO = 52
    private const val ANCHO_PRODUCTO = 24

    /**
     * Genera el texto del resumen: datos del pedido (fecha y estado), cliente, productos
     * (nombre, precio y cantidad), totales y notas. Quien lo llama decide dónde mostrarlo.
     */
    fun generar(pedido: Pedido): String = buildString {
        val linea: String = "-".repeat(ANCHO)
        appendLine("=".repeat(ANCHO))
        appendLine("DuocPizza · Pedido #${pedido.id}".padStart((ANCHO + 26) / 2))
        appendLine("=".repeat(ANCHO))
        appendLine("Fecha:     ${Formato.fecha(pedido.fecha)}")
        appendLine("Estado:    ${pedido.estadoVisible}")
        appendLine(linea)
        appendLine("CLIENTE")
        appendLine("Nombre:    ${pedido.cliente.nombre}")
        appendLine("Teléfono:  ${pedido.cliente.telefono.ifBlank { "-" }}")
        appendLine("Email:     ${pedido.cliente.email}")
        appendLine("Dirección: ${pedido.cliente.direccion.ifBlank { "-" }}")
        appendLine(linea)
        appendLine("PRODUCTOS")
        if (pedido.estaVacio) {
            appendLine("(sin productos)")
        } else {
            appendLine(fila("Producto", "Precio", "Cant.", "Subtotal"))
            for ((producto, cantidad) in pedido.cantidades) {
                appendLine(
                    fila(
                        producto.nombreCompleto.take(ANCHO_PRODUCTO),
                        Formato.precio(producto.precio),
                        "$cantidad",
                        Formato.precio(producto.precio * cantidad),
                    )
                )
            }
            appendLine("Unidades: ${pedido.cantidadUnidades}")
        }
        appendLine(linea)
        val totales: Totales = pedido.calcularTotal()
        appendLine(alinear("Subtotal", Formato.precio(totales.subtotal)))
        val descuento: String = if (totales.descuento > 0) {
            "Descuento $PORCENTAJE_DESCUENTO% (más de $PIZZAS_PARA_DESCUENTO pizzas)"
        } else {
            "Descuento (no aplica)"
        }
        appendLine(alinear(descuento, Formato.precio(-totales.descuento)))
        appendLine(alinear("IVA $PORCENTAJE_IVA%", Formato.precio(totales.impuesto)))
        appendLine(alinear("TOTAL", Formato.precio(totales.total)))
        appendLine(linea)
        append("Notas:     ${pedido.notas ?: "Sin notas"}")
    }

    /** Fila de la tabla de productos: nombre a la izquierda y columnas numéricas alineadas a la derecha. */
    private fun fila(producto: String, precio: String, cantidad: String, subtotal: String): String =
        "${producto.padEnd(ANCHO_PRODUCTO)}${precio.padStart(9)}${cantidad.padStart(6)}${subtotal.padStart(ANCHO - ANCHO_PRODUCTO - 15)}"

    /** Texto a la izquierda y monto a la derecha dentro del ancho del resumen. */
    private fun alinear(izquierda: String, derecha: String): String =
        izquierda + " ".repeat(maxOf(1, ANCHO - izquierda.length - derecha.length)) + derecha
}
