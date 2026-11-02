package cl.duoc.pizza.servicio

/** Resultado de comparar lo que se necesita de un ingrediente con lo que hay en el inventario. */
enum class NivelIngrediente {
    SUFICIENTE,
    INSUFICIENTE,
    AGOTADO,
}

/** Ingrediente que no alcanza para un pedido. */
data class IngredienteFaltante(
    val nombre: String,
    val necesario: Int,
    val disponible: Int,
    val nivel: NivelIngrediente,
) {
    val faltan: Int
        get() = necesario - disponible
}
