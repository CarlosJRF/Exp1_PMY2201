package cl.duoc.pizza.modelo

/**
 * Producto del catálogo. Cada tamaño de una pizza es un producto distinto,
 * así el stock se controla por producto.
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: Categoria,
    val precio: Int,
    val tamano: Tamano? = null,
) {
    init {
        require(precio > 0) { "El precio debe ser mayor a 0" }
    }

    val nombreCompleto: String
        get() = if (tamano != null) "$nombre ${tamano.nombreVisible}" else nombre
}
