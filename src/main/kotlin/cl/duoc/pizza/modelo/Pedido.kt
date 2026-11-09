package cl.duoc.pizza.modelo

import java.time.LocalDateTime

/**
 * Pedido de un cliente. Mientras [estado] es null funciona como carrito;
 * la validación de stock la hace PedidoServicio. Los cambios de estado, notas,
 * repartidor y entrega se hacen solo con métodos que validan la coherencia del pedido.
 */
class Pedido(
    val id: Int,
    val cliente: Cliente,
    val fecha: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        const val LARGO_MAXIMO_NOTAS = 200
    }

    /** Unidades de cada producto (producto -> cantidad), en el orden en que se agregaron. */
    private val _cantidades: MutableMap<Producto, Int> = linkedMapOf()

    var estado: EstadoPedido? = null
        private set

    /** Comentarios opcionales del cliente (por ejemplo, "sin cebolla"); null si no dejó notas. */
    var notas: String? = null
        private set

    /** Repartidor asignado cuando el pedido sale en camino. */
    var repartidor: Repartidor? = null
        private set

    var entregado: Boolean = false
        private set

    val estadoVisible: String
        get() = estado?.nombreVisible ?: "En carrito"

    /** Copia de las unidades por producto. */
    val cantidades: Map<Producto, Int>
        get() = _cantidades.toMap()

    /** Productos distintos del pedido. */
    val productos: List<Producto>
        get() = _cantidades.keys.toList()

    /** Suma de precio x unidades, antes de descuentos e impuestos. */
    val subtotal: Int
        get() = _cantidades.entries.sumOf { (producto, unidades) -> producto.precio * unidades }

    val cantidadPizzas: Int
        get() = _cantidades.filterKeys { it.categoria == Categoria.PIZZA }.values.sum()

    val total: Int
        get() = calcularTotal().total

    val cantidadUnidades: Int
        get() = _cantidades.values.sum()

    val estaVacio: Boolean
        get() = _cantidades.isEmpty()

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

    /** Guarda las notas mientras el pedido es un carrito; las notas en blanco quedan como null. */
    fun registrarNotas(notas: String?) {
        require(estado == null) { "Las notas solo se pueden registrar antes de confirmar el pedido #$id" }
        val limpias: String? = notas?.trim()?.ifBlank { null }
        require((limpias?.length ?: 0) <= LARGO_MAXIMO_NOTAS) {
            "Las notas pueden tener hasta $LARGO_MAXIMO_NOTAS caracteres"
        }
        this.notas = limpias
    }

    /** El pedido listo sale en camino con [repartidor]. */
    fun asignarRepartidor(repartidor: Repartidor) {
        require(estado == EstadoPedido.LISTO) { "Solo se asigna repartidor a un pedido listo (el #$id está ${estadoVisible.lowercase()})" }
        require(this.repartidor == null) { "El pedido #$id ya tiene repartidor" }
        cambiarEstado(EstadoPedido.EN_CAMINO)
        this.repartidor = repartidor
    }

    /** Registra la entrega de un pedido en camino. */
    fun marcarEntregado() {
        require(estado == EstadoPedido.EN_CAMINO && repartidor != null) { "El pedido #$id no está en camino" }
        require(!entregado) { "El pedido #$id ya fue entregado" }
        entregado = true
    }

    fun cantidadDe(producto: Producto): Int = _cantidades[producto] ?: 0

    fun agregar(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor a 0" }
        _cantidades[producto] = cantidadDe(producto) + cantidad
    }

    /** Quita la cantidad indicada; si es mayor o igual a la que hay, quita el producto del pedido. */
    fun quitar(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor a 0" }
        val actual: Int = cantidadDe(producto)
        require(actual > 0) { "El producto no está en el pedido" }
        if (cantidad >= actual) _cantidades.remove(producto) else _cantidades[producto] = actual - cantidad
    }
}
