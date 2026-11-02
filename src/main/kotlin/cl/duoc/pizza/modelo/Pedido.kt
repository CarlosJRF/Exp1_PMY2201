package cl.duoc.pizza.modelo

import java.time.LocalDateTime

/**
 * Pedido de un cliente. Mientras [estado] es null funciona como carrito;
 * la validación de stock la hace PedidoServicio.
 */
class Pedido(
    val id: Int,
    val cliente: Cliente,
    val fecha: LocalDateTime = LocalDateTime.now(),
) {
    /** Un elemento por unidad pedida: 2 Margaritas son 2 elementos. */
    private val _productos: MutableList<Producto> = mutableListOf()
    val productos: List<Producto>
        get() = _productos.toList()

    var estado: EstadoPedido? = null
        private set

    /** Repartidor asignado cuando el pedido sale en camino. */
    var repartidor: Repartidor? = null

    var entregado: Boolean = false

    val estadoVisible: String
        get() = estado?.nombreVisible ?: "En carrito"

    /** Unidades de cada producto, en el orden en que se agregaron. */
    val cantidades: Map<Producto, Int>
        get() = _productos.groupingBy { it }.eachCount()

    /** Suma de los precios, antes de descuentos e impuestos. */
    val subtotal: Int
        get() = _productos.sumOf { it.precio }

    val cantidadPizzas: Int
        get() = _productos.count { it.categoria == Categoria.PIZZA }

    val total: Int
        get() = calcularTotal().total

    val cantidadUnidades: Int
        get() = _productos.size

    val estaVacio: Boolean
        get() = _productos.isEmpty()

    /** Subtotal, descuento, IVA y total del pedido (ver la función calcularTotal de Totales.kt). */
    fun calcularTotal(): Totales = calcularTotal(subtotal, cantidadPizzas)

    /** Cambia el estado respetando el orden: carrito -> En preparación -> Listo -> En camino. */
    fun cambiarEstado(nuevo: EstadoPedido) {
        val siguiente: EstadoPedido? = when (estado) {
            null -> EstadoPedido.EN_PREPARACION
            EstadoPedido.EN_PREPARACION -> EstadoPedido.LISTO
            EstadoPedido.LISTO -> EstadoPedido.EN_CAMINO
            EstadoPedido.EN_CAMINO -> null
        }
        require(nuevo == siguiente) { "El pedido #$id no puede pasar de $estadoVisible a ${nuevo.nombreVisible}" }
        estado = nuevo
    }

    fun cantidadDe(producto: Producto): Int = _productos.count { it.id == producto.id }

    fun agregar(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor a 0" }
        repeat(cantidad) { _productos.add(producto) }
    }

    /** Quita la cantidad indicada; si es mayor a la que hay, quita todas las unidades del producto. */
    fun quitar(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor a 0" }
        require(cantidadDe(producto) > 0) { "El producto no está en el pedido" }
        repeat(minOf(cantidad, cantidadDe(producto))) { _productos.remove(producto) }
    }
}
