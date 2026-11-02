package cl.duoc.pizza.modelo

import java.time.LocalDateTime

/**
 * Pedido de un usuario. Mientras [estado] es null funciona como carrito;
 * la validación de stock y los cambios de estado los hace PedidoServicio.
 */
class Pedido(
    val id: Int,
    val usuario: Usuario,
    val fecha: LocalDateTime = LocalDateTime.now(),
) {
    private val _items: MutableList<ItemPedido> = mutableListOf()
    val items: List<ItemPedido>
        get() = _items.toList()

    var estado: EstadoPedido? = null

    /** Repartidor asignado cuando el pedido sale en camino. */
    var repartidor: Repartidor? = null

    val estadoVisible: String
        get() = estado?.nombreVisible ?: "En carrito"

    /** Suma de los subtotales, antes de descuentos e impuestos. */
    val subtotal: Int
        get() = _items.sumOf { it.subtotal }

    val cantidadPizzas: Int
        get() = _items.filter { it.producto.categoria == Categoria.PIZZA }.sumOf { it.cantidad }

    /** Subtotal, descuento, IVA y total según [calcularTotal]. */
    val totales: Totales
        get() = calcularTotal(subtotal, cantidadPizzas)

    /** Monto final a pagar. */
    val total: Int
        get() = totales.total

    val cantidadUnidades: Int
        get() = _items.sumOf { it.cantidad }

    val estaVacio: Boolean
        get() = _items.isEmpty()

    fun cantidadDe(producto: Producto): Int =
        _items.find { it.producto.id == producto.id }?.cantidad ?: 0

    /** Agrega el producto; si ya estaba en el pedido, suma la cantidad. */
    fun agregar(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor a 0" }
        val actual: Int = cantidadDe(producto)
        _items.removeIf { it.producto.id == producto.id }
        _items.add(ItemPedido(producto, actual + cantidad))
    }

    /** Quita la cantidad indicada; si llega a 0, elimina el producto del pedido. */
    fun quitar(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor a 0" }
        val actual: Int = cantidadDe(producto)
        require(actual > 0) { "El producto no está en el pedido" }
        val indice: Int = _items.indexOfFirst { it.producto.id == producto.id }
        if (cantidad >= actual) {
            _items.removeAt(indice)
        } else {
            _items[indice] = ItemPedido(producto, actual - cantidad)
        }
    }
}
