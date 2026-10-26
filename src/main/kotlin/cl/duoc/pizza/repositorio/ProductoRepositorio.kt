package cl.duoc.pizza.repositorio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Producto

/** Catálogo de solo lectura; los productos se cargan al crear el repositorio. */
class ProductoRepositorio(productos: List<Producto>) {
    private val productos: Map<Int, Producto> = productos.associateBy { it.id }

    fun buscarPorId(id: Int): Producto? = productos[id]

    fun listar(): List<Producto> = productos.values.sortedBy { it.id }

    fun listarPorCategoria(categoria: Categoria): List<Producto> =
        listar().filter { it.categoria == categoria }
}
