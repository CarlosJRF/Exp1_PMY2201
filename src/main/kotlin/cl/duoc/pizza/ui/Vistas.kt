package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.util.Formato

/** Textos que se muestran en más de un menú. */
object Vistas {

    fun catalogo(inventario: InventarioServicio): String = buildString {
        Categoria.entries.forEach { categoria ->
            appendLine("-- ${categoria.nombreVisible} --")
            inventario.catalogo().filter { it.categoria == categoria }.forEach { producto ->
                val stock = inventario.stockDe(producto)
                val aviso = when {
                    stock == 0 -> "AGOTADO"
                    inventario.tieneStockBajo(producto) -> "¡Quedan $stock!"
                    else -> "stock: $stock"
                }
                val id = producto.id.toString().padStart(2)
                appendLine("[$id] ${producto.nombreCompleto.padEnd(26)} ${Formato.precio(producto.precio).padStart(8)}  ($aviso)")
            }
        }
    }.trimEnd()

    fun lineaPedido(pedido: Pedido): String =
        "#${pedido.id}  ${Formato.fecha(pedido.fecha)}  ${pedido.usuario.email.padEnd(24)} " +
            "${Formato.precio(pedido.total).padStart(9)}  ${pedido.estado.nombreVisible}"
}
