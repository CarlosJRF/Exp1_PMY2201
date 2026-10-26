package cl.duoc.pizza.modelo

import cl.duoc.pizza.util.Formato

/** Descuento aplicado a un pedido: la regla que se cumplió, su porcentaje y el monto rebajado. */
data class Descuento(
    val descripcion: String,
    val porcentaje: Int,
    val monto: Int,
) {

    /**
     * Reglas de descuento. No son acumulables: si se cumplen varias, se aplica la de mayor porcentaje.
     *  - 3 o más pizzas en el pedido: 15 %.
     *  - Subtotal desde $30.000: 10 %.
     */
    companion object {
        const val PIZZAS_MINIMAS = 3
        const val PORCENTAJE_POR_PIZZAS = 15
        const val SUBTOTAL_MINIMO = 30_000
        const val PORCENTAJE_POR_MONTO = 10

        /** Devuelve el descuento que corresponde a los items, o null si no se cumple ninguna regla. */
        fun calcular(items: List<ItemPedido>): Descuento? {
            val subtotal: Int = items.sumOf { it.subtotal }
            val pizzas: Int = items.filter { it.producto.categoria == Categoria.PIZZA }.sumOf { it.cantidad }

            val (descripcion: String, porcentaje: Int) = when {
                pizzas >= PIZZAS_MINIMAS -> "$PIZZAS_MINIMAS o más pizzas" to PORCENTAJE_POR_PIZZAS
                subtotal >= SUBTOTAL_MINIMO -> "compra desde ${Formato.precio(SUBTOTAL_MINIMO)}" to PORCENTAJE_POR_MONTO
                else -> return null
            }
            return Descuento(descripcion, porcentaje, porcentajeDe(subtotal, porcentaje))
        }

        /** Porcentaje de un monto en pesos, redondeado al peso más cercano (sin usar Double). */
        fun porcentajeDe(monto: Int, porcentaje: Int): Int = (monto * porcentaje + 50) / 100
    }
}
