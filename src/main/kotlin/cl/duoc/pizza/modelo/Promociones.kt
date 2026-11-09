package cl.duoc.pizza.modelo

import java.time.LocalDate

/** Promoción de temporada: rango de fechas y descuento sobre los productos @Promocionable. */
object Promociones {
    const val NOMBRE = "Noviembre Pizzero"

    /** Rango cerrado de fechas (ambos extremos incluidos) en que la promoción está vigente. */
    val PERIODO: ClosedRange<LocalDate> = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 30)

    fun vigente(fecha: LocalDate, periodo: ClosedRange<LocalDate> = PERIODO): Boolean = fecha in periodo

    /**
     * Descuento de la promoción para las [cantidades] de un pedido hecho en [fecha]: si la fecha está en el
     * periodo, cada producto promocionable rebaja el porcentaje de su anotación. Fuera del periodo es 0.
     */
    fun descuentoDe(cantidades: Map<Producto, Int>, fecha: LocalDate, periodo: ClosedRange<LocalDate> = PERIODO): Int {
        if (!vigente(fecha, periodo)) return 0
        return cantidades.entries.sumOf { (producto, unidades) ->
            producto.promocion?.let { promocion -> porcentajeDe(producto.precio * unidades, promocion.porcentaje) } ?: 0
        }
    }
}
