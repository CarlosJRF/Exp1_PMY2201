package cl.duoc.pizza.modelo

import java.time.LocalDateTime

/**
 * Pedido de un usuario. Mientras está PENDIENTE funciona como carrito;
 * la validación de stock y los cambios de estado los hace PedidoServicio.
 */
class Pedido(
    val id: Int,
    val usuario: Usuario,
    val fecha: LocalDateTime = LocalDateTime.now(),
) {
    private val _items = mutableListOf<ItemPedido>()
    val items: List<ItemPedido>
        get() = _items.toList()

    var estado: EstadoPedido = EstadoPedido.PENDIENTE

    val total: Int
        get() = _items.sumOf { it.subtotal }

    val estaVacio: Boolean
        get() = _items.isEmpty()

    fun cantidadDe(producto: Producto): Int =
        _items.find { it.producto.id == producto.id }?.cantidad ?: 0

    /** Agrega el producto; si ya estaba en el pedido, suma la cantidad. */
    fun agregar(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor a 0" }
        val actual = cantidadDe(producto)
        _items.removeIf { it.producto.id == producto.id }
        _items.add(ItemPedido(producto, actual + cantidad))
    }

    /** Quita la cantidad indicada; si llega a 0, elimina el producto del pedido. */
    fun quitar(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor a 0" }
        val actual = cantidadDe(producto)
        require(actual > 0) { "El producto no está en el pedido" }
        val indice = _items.indexOfFirst { it.producto.id == producto.id }
        if (cantidad >= actual) {
            _items.removeAt(indice)
        } else {
            _items[indice] = ItemPedido(producto, actual - cantidad)
        }
    }
}
