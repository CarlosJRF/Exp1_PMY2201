package cl.duoc.pizza.modelo

enum class EstadoPedido(val nombreVisible: String) {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    CANCELADO("Cancelado"),
}
