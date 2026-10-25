package cl.duoc.pizza.modelo

data class ItemPedido(
    val producto: Producto,
    val cantidad: Int,
) {
    init {
        require(cantidad > 0) { "La cantidad debe ser mayor a 0" }
    }

    val subtotal: Int
        get() = producto.precio * cantidad
}
