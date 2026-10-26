package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.repositorio.ProductoRepositorio
import cl.duoc.pizza.repositorio.StockRepositorio

class InventarioServicio(
    private val productoRepositorio: ProductoRepositorio,
    private val stockRepositorio: StockRepositorio,
) {

    companion object {
        const val UMBRAL_STOCK_BAJO = 3
    }

    fun catalogo(): List<Producto> = productoRepositorio.listar()

    fun buscarProducto(id: Int): Producto =
        productoRepositorio.buscarPorId(id) ?: throw ErrorNegocio("No existe un producto con id $id")

    fun stockDe(producto: Producto): Int = stockRepositorio.obtener(producto.id)

    fun hayStock(producto: Producto, cantidad: Int): Boolean = stockDe(producto) >= cantidad

    fun tieneStockBajo(producto: Producto): Boolean = stockDe(producto) <= UMBRAL_STOCK_BAJO

    fun productosConStockBajo(): List<Producto> = catalogo().filter { tieneStockBajo(it) }

    /** Texto de disponibilidad para mostrar al usuario: agotado, stock bajo o unidades disponibles. */
    fun disponibilidad(producto: Producto): String {
        val stock: Int = stockDe(producto)
        return when {
            stock == 0 -> "AGOTADO"
            tieneStockBajo(producto) -> "¡Quedan $stock!"
            else -> "stock: $stock"
        }
    }

    /** Mensaje claro cuando no alcanzan las unidades: distingue un producto agotado de uno con poco stock. */
    fun mensajeFaltaStock(producto: Producto, disponible: Int = stockDe(producto)): String =
        if (disponible <= 0) {
            "${producto.nombreCompleto} está agotado"
        } else {
            "Stock insuficiente de ${producto.nombreCompleto}: solo hay $disponible disponible(s)"
        }

    fun descontar(producto: Producto, cantidad: Int) {
        validarCantidad(cantidad)
        val disponible: Int = stockDe(producto)
        if (disponible < cantidad) throw ErrorNegocio(mensajeFaltaStock(producto, disponible))
        stockRepositorio.actualizar(producto.id, disponible - cantidad)
    }

    fun reponer(producto: Producto, cantidad: Int) {
        validarCantidad(cantidad)
        stockRepositorio.actualizar(producto.id, stockDe(producto) + cantidad)
    }

    private fun validarCantidad(cantidad: Int) {
        if (cantidad <= 0) throw ErrorNegocio("La cantidad debe ser mayor a 0")
    }
}
