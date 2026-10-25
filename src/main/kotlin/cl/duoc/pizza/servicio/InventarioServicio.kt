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

    fun descontar(producto: Producto, cantidad: Int) {
        validarCantidad(cantidad)
        val disponible = stockDe(producto)
        if (disponible < cantidad) {
            throw ErrorNegocio("Stock insuficiente de ${producto.nombreCompleto} (disponible: $disponible)")
        }
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
