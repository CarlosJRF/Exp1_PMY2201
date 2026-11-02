package cl.duoc.pizza.modelo

const val PORCENTAJE_IVA = 19
const val PORCENTAJE_DESCUENTO = 10

/** Se aplica el descuento cuando el pedido tiene más de esta cantidad de pizzas. */
const val PIZZAS_PARA_DESCUENTO = 3

/** Desglose del total de un pedido, en pesos. */
data class Totales(
    val subtotal: Int,
    val descuento: Int,
    val impuesto: Int,
    val total: Int,
)

/**
 * Calcula el total de un pedido: primero el descuento (10 % si hay más de 3 pizzas)
 * y luego el IVA (19 %) sobre el monto ya descontado.
 */
fun calcularTotal(subtotal: Int, cantidadPizzas: Int): Totales {
    val descuento: Int = if (cantidadPizzas > PIZZAS_PARA_DESCUENTO) porcentajeDe(subtotal, PORCENTAJE_DESCUENTO) else 0
    val neto: Int = subtotal - descuento
    val impuesto: Int = porcentajeDe(neto, PORCENTAJE_IVA)
    return Totales(subtotal, descuento, impuesto, neto + impuesto)
}

/** Porcentaje de un monto en pesos, redondeado al peso más cercano (sin usar Double). */
fun porcentajeDe(monto: Int, porcentaje: Int): Int = (monto * porcentaje + 50) / 100
