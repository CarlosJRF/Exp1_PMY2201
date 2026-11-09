package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.PIZZAS_PARA_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_IVA
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Promociones
import cl.duoc.pizza.modelo.Totales
import cl.duoc.pizza.util.Formato

object ResumenPedido {
    /** Ancho total de cada línea del resumen. */
    private const val ANCHO = 52

    /** Columnas de la tabla de productos: el subtotal ocupa lo que queda del ancho. */
    private const val ANCHO_PRODUCTO = 24
    private const val ANCHO_PRECIO = 9
    private const val ANCHO_CANTIDAD = 6
    private const val ANCHO_SUBTOTAL = ANCHO - ANCHO_PRODUCTO - ANCHO_PRECIO - ANCHO_CANTIDAD

    /**
     * Genera el texto del resumen: datos del pedido (fecha y estado), cliente, productos
     * (nombre, precio y cantidad), totales y notas. Quien lo llama decide dónde mostrarlo.
     */
    fun generar(pedido: Pedido): String = buildString {
        val linea: String = "-".repeat(ANCHO)
        appendLine("=".repeat(ANCHO))
        appendLine(centrar("DuocPizza · Pedido #${pedido.id}"))
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
                        producto.nombre.take(ANCHO_PRODUCTO),
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
        val promocion: String = when {
            totales.promocion > 0 -> "Promoción ${Promociones.NOMBRE}"
            pedido.enPromocion -> "Promoción (sin productos en promo)"
            else -> "Promoción (fuera de periodo)"
        }
        appendLine(alinear(promocion, Formato.precio(-totales.promocion)))
        appendLine(alinear("IVA $PORCENTAJE_IVA%", Formato.precio(totales.impuesto)))
        appendLine(alinear("TOTAL", Formato.precio(totales.total)))
        appendLine(linea)
        append("Notas:     ${pedido.notas ?: "Sin notas"}")
    }

    /** Fila de la tabla de productos: nombre a la izquierda y columnas numéricas alineadas a la derecha. */
    private fun fila(producto: String, precio: String, cantidad: String, subtotal: String): String =
        producto.padEnd(ANCHO_PRODUCTO) + precio.padStart(ANCHO_PRECIO) +
            cantidad.padStart(ANCHO_CANTIDAD) + subtotal.padStart(ANCHO_SUBTOTAL)

    /** Centra el texto en el ancho del resumen. */
    private fun centrar(texto: String): String = texto.padStart((ANCHO + texto.length) / 2)

    /** Texto a la izquierda y monto a la derecha dentro del ancho del resumen. */
    private fun alinear(izquierda: String, derecha: String): String =
        izquierda + " ".repeat(maxOf(1, ANCHO - izquierda.length - derecha.length)) + derecha
}
