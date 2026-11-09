package cl.duoc.pizza.modelo

const val PORCENTAJE_IVA = 19
const val PORCENTAJE_DESCUENTO = 10

/** Se aplica el descuento cuando el pedido tiene más de esta cantidad de pizzas. */
const val PIZZAS_PARA_DESCUENTO = 3

/** Desglose del total de un pedido, en pesos. [promocion] es el descuento de temporada (ver [Promociones]). */
data class Totales(
    val subtotal: Int,
    val descuento: Int,
    val impuesto: Int,
    val total: Int,
    val promocion: Int = 0,
)

/**
 * Calcula el total de un pedido: primero el descuento (10 % si hay más de 3 pizzas) y la promoción
 * de temporada ([descuentoPromocion], ya calculado), y luego el IVA (19 %) sobre el monto descontado.
 */
fun calcularTotal(subtotal: Int, cantidadPizzas: Int, descuentoPromocion: Int = 0): Totales {
    require(subtotal >= 0) { "Costo de pedido no válido: el subtotal no puede ser negativo ($subtotal)" }
    require(cantidadPizzas >= 0) { "La cantidad de pizzas no puede ser negativa ($cantidadPizzas)" }
    val descuento: Int = if (cantidadPizzas > PIZZAS_PARA_DESCUENTO) porcentajeDe(subtotal, PORCENTAJE_DESCUENTO) else 0
    require(descuentoPromocion in 0..subtotal - descuento) { "Descuento de promoción no válido ($descuentoPromocion)" }
    val neto: Int = subtotal - descuento - descuentoPromocion
    val impuesto: Int = porcentajeDe(neto, PORCENTAJE_IVA)
    return Totales(subtotal, descuento, impuesto, neto + impuesto, descuentoPromocion)
}

/** Porcentaje de un monto en pesos, redondeado al peso más cercano (sin usar Double). */
fun porcentajeDe(monto: Int, porcentaje: Int): Int = (monto * porcentaje + 50) / 100
