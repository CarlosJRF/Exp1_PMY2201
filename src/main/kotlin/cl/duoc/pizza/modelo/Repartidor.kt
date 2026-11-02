package cl.duoc.pizza.modelo

/** Repartidor de la pizzería; [distanciaKm] es lo que está de la pizzería. */
class Repartidor(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val distanciaKm: Double,
) {
    var disponible: Boolean = true
}
