package cl.duoc.pizza.modelo

/** Estado de un pedido confirmado. Mientras no se confirma, el pedido es un carrito (estado null). */
enum class EstadoPedido(val nombreVisible: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
}
