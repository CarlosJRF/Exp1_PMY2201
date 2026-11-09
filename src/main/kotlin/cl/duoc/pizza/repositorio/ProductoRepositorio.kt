package cl.duoc.pizza.repositorio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Producto

/** Catálogo de productos. No admite productos repetidos (mismo nombre y categoría, ver Producto.equals). */
class ProductoRepositorio(productosIniciales: List<Producto>) {
    private val productos: MutableMap<Int, Producto> = linkedMapOf()

    init {
        productosIniciales.forEach { producto ->
            require(agregar(producto)) { "Producto repetido en el catálogo: $producto" }
        }
    }

    /** Agrega el producto si no hay otro igual ni otro con su id; devuelve false si lo rechaza. */
    fun agregar(producto: Producto): Boolean {
        if (producto in productos.values || producto.id in productos) return false
        productos[producto.id] = producto
        return true
    }

    fun buscarPorId(id: Int): Producto? = productos[id]

    fun listar(): List<Producto> = productos.values.sortedBy { it.id }

    fun listarPorCategoria(categoria: Categoria): List<Producto> =
        listar().filter { it.categoria == categoria }
}
