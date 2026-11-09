package cl.duoc.pizza.repositorio

/** Cantidad disponible por nombre de ingrediente. Las reglas de negocio están en InventarioServicio. */
class StockRepositorio(stockInicial: Map<String, Int> = emptyMap()) {
    private val stock: MutableMap<String, Int> = stockInicial.toMutableMap()

    init {
        require(stock.values.all { it >= 0 }) { "El stock inicial no puede ser negativo" }
    }

    fun obtener(ingrediente: String): Int = stock[ingrediente] ?: 0

    fun actualizar(ingrediente: String, cantidad: Int) {
        aplicar(mapOf(ingrediente to cantidad - obtener(ingrediente)))
    }

    /**
     * Suma cada cambio (positivo o negativo) a su ingrediente como una sola operación atómica:
     * primero calcula todos los valores nuevos en una copia y los valida; si alguno quedaría negativo
     * no modifica nada. Si la escritura fallara a medias, restaura la copia anterior.
     * Es @Synchronized para que dos operaciones no se mezclen.
     */
    @Synchronized
    fun aplicar(cambios: Map<String, Int>) {
        val nuevos: Map<String, Int> = cambios.mapValues { (ingrediente, cambio) -> obtener(ingrediente) + cambio }
        val negativos: Map<String, Int> = nuevos.filterValues { it < 0 }
        require(negativos.isEmpty()) { "El stock no puede ser negativo: ${negativos.keys.joinToString()}" }

        val respaldo: Map<String, Int> = stock.toMap()
        try {
            stock.putAll(nuevos)
        } catch (e: RuntimeException) {
            stock.clear()
            stock.putAll(respaldo)
            throw e
        }
    }

    fun listar(): Map<String, Int> = stock.toMap()
}
