package cl.duoc.pizza.modelo

/** Repartidor de la pizzería; [distanciaKm] es lo que está de la pizzería. */
class Repartidor(
    id: Int,
    nombre: String,
    telefono: String,
    email: String,
    val distanciaKm: Double,
) : Usuario(id, nombre, telefono, email) {

    var disponible: Boolean = true
        private set

    override fun descripcion(): String =
        "Repartidor: ${super.descripcion()} · a $distanciaKm km · ${if (disponible) "Disponible" else "En reparto"}"

    /** Toma un pedido listo: el pedido sale en camino y el repartidor queda ocupado. */
    fun tomarPedido(pedido: Pedido) {
        require(disponible) { "$nombre no está disponible" }
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        pedido.repartidor = this
        disponible = false
    }

    /** Entrega un pedido que lleva este repartidor y queda disponible otra vez. */
    fun entregarPedido(pedido: Pedido) {
        require(pedido.repartidor === this) { "El pedido #${pedido.id} no lo lleva $nombre" }
        require(!pedido.entregado) { "El pedido #${pedido.id} ya fue entregado" }
        pedido.entregado = true
        disponible = true
    }
}
