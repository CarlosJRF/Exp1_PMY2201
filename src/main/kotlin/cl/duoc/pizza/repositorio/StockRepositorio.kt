package cl.duoc.pizza.repositorio

/** Unidades disponibles por id de producto. Las reglas de negocio están en InventarioServicio. */
class StockRepositorio(stockInicial: Map<Int, Int> = emptyMap()) {
    private val stock = stockInicial.toMutableMap()

    init {
        require(stock.values.all { it >= 0 }) { "El stock inicial no puede ser negativo" }
    }

    fun obtener(idProducto: Int): Int = stock[idProducto] ?: 0

    fun actualizar(idProducto: Int, cantidad: Int) {
        require(cantidad >= 0) { "El stock no puede ser negativo" }
        stock[idProducto] = cantidad
    }

    fun listar(): Map<Int, Int> = stock.toMap()
}
