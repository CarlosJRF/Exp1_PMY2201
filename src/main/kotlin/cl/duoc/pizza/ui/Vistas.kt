package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.PIZZAS_PARA_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_DESCUENTO
import cl.duoc.pizza.modelo.PORCENTAJE_IVA
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Promocionable
import cl.duoc.pizza.modelo.Promociones
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.util.Formato
import java.time.LocalDate

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
                val promo: String = if (producto.esPromocionable) " ★" else ""
                appendLine("[$id] ${producto.nombre.padEnd(26)} $precio  (${inventario.disponibilidad(producto)})$promo")
            }
        }
    }.trimEnd()

    /** Ingredientes numerados con su cantidad disponible. */
    fun ingredientes(inventario: InventarioServicio): String =
        inventario.ingredientes().entries.withIndex().joinToString("\n") { (indice, entrada) ->
            "${(indice + 1).toString().padStart(2)}. ${entrada.key.padEnd(20)} ${entrada.value.toString().padStart(4)}"
        }

    /**
     * Explica los descuentos y el impuesto. Los porcentajes de la promoción de temporada se leen con reflection
     * de las anotaciones @Promocionable de los productos del catálogo (un Set evita repetirlos).
     */
    fun promociones(inventario: InventarioServicio, hoy: LocalDate = LocalDate.now()): String {
        val promociones: Set<Promocionable> = inventario.catalogo().mapNotNull { it.promocion }.toSet()
        val periodo: String = "${Formato.dia(Promociones.PERIODO.start)} al ${Formato.dia(Promociones.PERIODO.endInclusive)}"
        val estado: String = if (Promociones.vigente(hoy)) "vigente" else "no vigente hoy"
        return "Más de $PIZZAS_PARA_DESCUENTO pizzas = $PORCENTAJE_DESCUENTO% dcto. · Precios sin IVA ($PORCENTAJE_IVA%).\n" +
            "★ ${Promociones.NOMBRE} ($periodo, $estado): ${promociones.joinToString { it.descripcion }}."
    }

    fun lineaPedido(pedido: Pedido): String =
        "#${pedido.id}  ${Formato.fecha(pedido.fecha)}  ${pedido.cliente.email.padEnd(24)} " +
            "${Formato.precio(pedido.total).padStart(9)}  ${pedido.estadoVisible}" +
            (pedido.repartidor?.let { " · ${it.nombre}" } ?: "") +
            if (pedido.entregado) " (entregado)" else ""
}
