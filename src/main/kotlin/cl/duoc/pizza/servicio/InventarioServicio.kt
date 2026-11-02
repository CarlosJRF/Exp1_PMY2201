package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.repositorio.ProductoRepositorio
import cl.duoc.pizza.repositorio.StockRepositorio

/** Inventario por ingredientes: los productos se pueden vender mientras alcancen sus ingredientes. */
class InventarioServicio(
    private val productoRepositorio: ProductoRepositorio,
    private val stockRepositorio: StockRepositorio,
) {

    companion object {
        const val UMBRAL_STOCK_BAJO = 5
    }

    fun catalogo(): List<Producto> = productoRepositorio.listar()

    fun buscarProducto(id: Int): Producto =
        productoRepositorio.buscarPorId(id) ?: throw ErrorNegocio("No existe un producto con id $id")

    /** Ingredientes del inventario con su cantidad disponible. */
    fun ingredientes(): Map<String, Int> = stockRepositorio.listar()

    fun stockDe(ingrediente: String): Int = stockRepositorio.obtener(ingrediente)

    fun ingredientesConStockBajo(): List<String> =
        ingredientes().filter { it.value <= UMBRAL_STOCK_BAJO }.keys.toList()

    /** Cantidad de cada ingrediente para preparar un lote de [cantidadPizzas], según lo que usa una pizza. */
    fun cantidadParaLote(ingredientes: Map<String, Int>, cantidadPizzas: Int): Map<String, Int> =
        ingredientes.mapValues { (_, porUnidad) -> porUnidad * cantidadPizzas }

    /** Total de cada ingrediente que necesitan los productos (producto -> unidades). */
    fun necesarios(cantidades: Map<Producto, Int>): Map<String, Int> {
        val total: MutableMap<String, Int> = mutableMapOf()
        for ((producto, unidades) in cantidades) {
            for ((ingrediente, cantidad) in cantidadParaLote(producto.ingredientes, unidades)) {
                total[ingrediente] = (total[ingrediente] ?: 0) + cantidad
            }
        }
        return total
    }

    /** Unidades del producto que se pueden preparar con el stock, sin contar lo [reservado] por otros productos. */
    fun unidadesDisponibles(producto: Producto, reservado: Map<String, Int> = emptyMap()): Int =
        producto.ingredientes.minOf { (ingrediente, porUnidad) ->
            maxOf(0, stockDe(ingrediente) - (reservado[ingrediente] ?: 0)) / porUnidad
        }

    fun hayStock(producto: Producto, cantidad: Int): Boolean = unidadesDisponibles(producto) >= cantidad

    fun tieneStockBajo(producto: Producto): Boolean = unidadesDisponibles(producto) <= UMBRAL_STOCK_BAJO

    /** Texto de disponibilidad para el catálogo: agotado, stock bajo o unidades que se pueden preparar. */
    fun disponibilidad(producto: Producto): String {
        val unidades: Int = unidadesDisponibles(producto)
        return when {
            unidades == 0 -> "AGOTADO"
            unidades <= UMBRAL_STOCK_BAJO -> "¡Quedan $unidades!"
            else -> "stock: $unidades"
        }
    }

    fun nivelDe(ingrediente: String, necesario: Int): NivelIngrediente {
        val disponible: Int = stockDe(ingrediente)
        return when {
            disponible == 0 -> NivelIngrediente.AGOTADO
            disponible < necesario -> NivelIngrediente.INSUFICIENTE
            else -> NivelIngrediente.SUFICIENTE
        }
    }

    /** Revisa cada ingrediente que necesitan los productos y devuelve los que no alcanzan (vacío si alcanza todo). */
    fun evaluarDisponibilidad(cantidades: Map<Producto, Int>): List<IngredienteFaltante> =
        faltantesDe(necesarios(cantidades))

    private fun faltantesDe(necesarios: Map<String, Int>): List<IngredienteFaltante> {
        val faltantes: MutableList<IngredienteFaltante> = mutableListOf()
        for ((ingrediente, necesario) in necesarios) {
            when (val nivel: NivelIngrediente = nivelDe(ingrediente, necesario)) {
                NivelIngrediente.SUFICIENTE -> continue
                NivelIngrediente.INSUFICIENTE, NivelIngrediente.AGOTADO ->
                    faltantes.add(IngredienteFaltante(ingrediente, necesario, stockDe(ingrediente), nivel))
            }
        }
        return faltantes
    }

    /** Mensaje con los ingredientes insuficientes y los pasos para reabastecerlos. */
    fun mensajeFaltantes(faltantes: List<IngredienteFaltante>): String = buildString {
        appendLine("Ingredientes insuficientes:")
        for (faltante in faltantes) {
            val detalle: String = when (faltante.nivel) {
                NivelIngrediente.AGOTADO -> "agotado (se necesitan ${faltante.necesario})"
                else -> "hay ${faltante.disponible}, se necesitan ${faltante.necesario} (faltan ${faltante.faltan})"
            }
            appendLine("  - ${faltante.nombre}: $detalle")
        }
        appendLine("Pasos para reabastecer:")
        appendLine("  1. Iniciar sesión como administrador.")
        appendLine("  2. Elegir \"Reponer ingrediente\" en el menú.")
        appendLine("  3. Reponer al menos: ${faltantes.joinToString { "${it.nombre} (${it.faltan})" }}.")
        append("  4. Volver a intentar el pedido.")
    }

    /**
     * Calcula lo que necesita un lote de [cantidadPizzas] y lo descuenta del inventario.
     * Si falta algún ingrediente no descuenta nada. Devuelve las cantidades usadas.
     */
    fun prepararLote(ingredientes: Map<String, Int>, cantidadPizzas: Int): Map<String, Int> {
        if (cantidadPizzas <= 0) throw ErrorNegocio("La cantidad de pizzas debe ser mayor a 0")
        val necesarios: Map<String, Int> = cantidadParaLote(ingredientes, cantidadPizzas)
        val faltantes: List<IngredienteFaltante> = faltantesDe(necesarios)
        if (faltantes.isNotEmpty()) throw ErrorNegocio(mensajeFaltantes(faltantes))

        for ((ingrediente, necesario) in necesarios) {
            stockRepositorio.actualizar(ingrediente, stockDe(ingrediente) - necesario)
        }
        return necesarios
    }

    /** Prepara cada producto como un lote. Si falta algún ingrediente para el total, no descuenta nada. */
    fun descontar(cantidades: Map<Producto, Int>) {
        val faltantes: List<IngredienteFaltante> = evaluarDisponibilidad(cantidades)
        if (faltantes.isNotEmpty()) throw ErrorNegocio(mensajeFaltantes(faltantes))
        for ((producto, unidades) in cantidades) {
            prepararLote(producto.ingredientes, unidades)
        }
    }

    /** Devuelve al inventario los ingredientes de los productos (por ejemplo, al cancelar un pedido). */
    fun devolver(cantidades: Map<Producto, Int>) {
        for ((ingrediente, cantidad) in necesarios(cantidades)) {
            stockRepositorio.actualizar(ingrediente, stockDe(ingrediente) + cantidad)
        }
    }

    fun reponer(ingrediente: String, cantidad: Int) {
        if (cantidad <= 0) throw ErrorNegocio("La cantidad debe ser mayor a 0")
        if (ingrediente !in ingredientes()) throw ErrorNegocio("No existe el ingrediente $ingrediente")
        stockRepositorio.actualizar(ingrediente, stockDe(ingrediente) + cantidad)
    }
}
