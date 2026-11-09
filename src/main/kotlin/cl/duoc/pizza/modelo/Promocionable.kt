package cl.duoc.pizza.modelo

/**
 * Marca una clase de producto como elegible para la promoción de temporada.
 * Se lee en tiempo de ejecución con reflection (por eso la retención es RUNTIME).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Promocionable(
    /** Porcentaje de descuento sobre el precio del producto mientras la promoción está vigente. */
    val porcentaje: Int,
    val descripcion: String,
)
