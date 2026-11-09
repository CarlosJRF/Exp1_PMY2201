package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.PIZZAS_PARA_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_IVA
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.util.Formato

/** Textos que se muestran en más de un menú. */
object Vistas {

    /** Catálogo agrupado por categoría. Se consulta una sola vez y solo se muestran las categorías que tienen productos. */
    fun catalogo(inventario: InventarioServicio): String = buildString {
        val catalogo: List<Producto> = inventario.catalogo()
        val categorias: Set<Categoria> = catalogo.map { it.categoria }.toSortedSet()
        for (categoria in categorias) {
            appendLine("-- ${categoria.nombreVisible} --")
            catalogo.filter { it.categoria == categoria }.forEach { producto ->
                val id: String = producto.id.toString().padStart(2)
                val precio: String = Formato.precio(producto.precio).padStart(8)
                appendLine("[$id] ${producto.nombreCompleto.padEnd(26)} $precio  (${inventario.disponibilidad(producto)})")
            }
        }
    }.trimEnd()

    /** Ingredientes numerados con su cantidad disponible. */
    fun ingredientes(inventario: InventarioServicio): String =
        inventario.ingredientes().entries.withIndex().joinToString("\n") { (indice, entrada) ->
            "${(indice + 1).toString().padStart(2)}. ${entrada.key.padEnd(20)} ${entrada.value.toString().padStart(4)}"
        }

    /** Explica el descuento y el impuesto vigentes (ver [cl.duoc.pizza.modelo.calcularTotal]). */
    fun promociones(): String =
        "Promoción: más de $PIZZAS_PARA_DESCUENTO pizzas = $PORCENTAJE_DESCUENTO% dcto. · Precios sin IVA ($PORCENTAJE_IVA%)."

    fun lineaPedido(pedido: Pedido): String =
        "#${pedido.id}  ${Formato.fecha(pedido.fecha)}  ${pedido.cliente.email.padEnd(24)} " +
            "${Formato.precio(pedido.total).padStart(9)}  ${pedido.estadoVisible}" +
            (pedido.repartidor?.let { " · ${it.nombre}" } ?: "") +
            if (pedido.entregado) " (entregado)" else ""
}
