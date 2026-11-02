package cl.duoc.pizza.modelo

/**
 * Producto del catálogo. Cada tamaño de una pizza es un producto distinto.
 * [ingredientes] indica cuánto de cada ingrediente se usa por unidad; si no se indica,
 * el producto es su propio ingrediente (bebidas y acompañamientos).
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: Categoria,
    val precio: Int,
    val tamano: Tamano? = null,
    val ingredientes: Map<String, Int> = mapOf(nombre to 1),
) {
    init {
        require(precio > 0) { "El precio debe ser mayor a 0" }
        require(ingredientes.isNotEmpty() && ingredientes.values.all { it > 0 }) {
            "El producto debe tener ingredientes con cantidades mayores a 0"
        }
    }

    val nombreCompleto: String
        get() = if (tamano != null) "$nombre ${tamano.nombreVisible}" else nombre
}
