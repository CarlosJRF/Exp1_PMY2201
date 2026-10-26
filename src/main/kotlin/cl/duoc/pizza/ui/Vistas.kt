package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Descuento
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.util.Formato

/** Textos que se muestran en más de un menú. */
object Vistas {

    fun catalogo(inventario: InventarioServicio): String = buildString {
        Categoria.entries.forEach { categoria ->
            appendLine("-- ${categoria.nombreVisible} --")
            inventario.catalogo().filter { it.categoria == categoria }.forEach { producto ->
                val id: String = producto.id.toString().padStart(2)
                val precio: String = Formato.precio(producto.precio).padStart(8)
                appendLine("[$id] ${producto.nombreCompleto.padEnd(26)} $precio  (${inventario.disponibilidad(producto)})")
            }
        }
    }.trimEnd()

    /** Explica las reglas de descuento vigentes (ver [Descuento]). */
    fun promociones(): String =
        "Promociones (no acumulables): ${Descuento.PIZZAS_MINIMAS} o más pizzas = ${Descuento.PORCENTAJE_POR_PIZZAS}% dcto. · " +
            "Compras desde ${Formato.precio(Descuento.SUBTOTAL_MINIMO)} = ${Descuento.PORCENTAJE_POR_MONTO}% dcto."

    fun lineaPedido(pedido: Pedido): String =
        "#${pedido.id}  ${Formato.fecha(pedido.fecha)}  ${pedido.usuario.email.padEnd(24)} " +
            "${Formato.precio(pedido.total).padStart(9)}  ${pedido.estado.nombreVisible}"
}
