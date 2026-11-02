package cl.duoc.pizza.repositorio

/** Cantidad disponible por nombre de ingrediente. Las reglas de negocio están en InventarioServicio. */
class StockRepositorio(stockInicial: Map<String, Int> = emptyMap()) {
    private val stock: MutableMap<String, Int> = stockInicial.toMutableMap()

    init {
        require(stock.values.all { it >= 0 }) { "El stock inicial no puede ser negativo" }
    }

    fun obtener(ingrediente: String): Int = stock[ingrediente] ?: 0

    fun actualizar(ingrediente: String, cantidad: Int) {
        require(cantidad >= 0) { "El stock no puede ser negativo" }
        stock[ingrediente] = cantidad
    }

    fun listar(): Map<String, Int> = stock.toMap()
}
