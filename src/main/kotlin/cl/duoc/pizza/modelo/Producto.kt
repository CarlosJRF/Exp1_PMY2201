package cl.duoc.pizza.modelo

import cl.duoc.pizza.util.Formato
import java.util.Objects
import kotlin.reflect.full.findAnnotation

/**
 * Producto del catálogo. Cada tipo concreto (Pizza, Combo, Bebida, Acompanamiento) es una subclase;
 * las que llevan [Promocionable] participan en la promoción de temporada.
 * [ingredientes] indica cuánto de cada ingrediente se usa por unidad; si no se indica,
 * el producto es su propio ingrediente (bebidas y acompañamientos).
 *
 * Dos productos son iguales (`==`) si tienen el mismo nombre y la misma categoría.
 */
abstract class Producto(
    val id: Int,
    val nombre: String,
    val categoria: Categoria,
    val precio: Int,
    val tamano: Tamano? = null,
    val ingredientes: Map<String, Int> = mapOf(nombre to 1),
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del producto es obligatorio" }
        require(precio > 0) { "El precio debe ser mayor a 0" }
        require(ingredientes.isNotEmpty() && ingredientes.values.all { it > 0 }) {
            "El producto debe tener ingredientes con cantidades mayores a 0"
        }
    }

    /** Promoción declarada con @Promocionable en la clase concreta, leída con reflection; null si no tiene. */
    val promocion: Promocionable?
        get() = this::class.findAnnotation<Promocionable>()

    val esPromocionable: Boolean
        get() = promocion != null

    /** Descripción completa: nombre, precio y categoría; cada subclase agrega su detalle. */
    open fun detallarProducto(): String = "$nombre (${Formato.precio(precio)}) · ${categoria.nombreVisible}"

    /** Sobrecarga de `==`: mismo producto si coinciden nombre (sin distinguir mayúsculas) y categoría. */
    override operator fun equals(other: Any?): Boolean =
        other is Producto && nombre.equals(other.nombre, ignoreCase = true) && categoria == other.categoria

    /** Coherente con [equals]: usa los mismos atributos clave. */
    override fun hashCode(): Int = Objects.hash(nombre.lowercase(), categoria)

    override fun toString(): String = "$nombre (${categoria.nombreVisible})"
}

/** Pizza; su nombre incluye el tamaño ("Margarita Mediana") porque cada tamaño es un producto distinto. */
@Promocionable(porcentaje = 10, descripcion = "10% en pizzas")
class Pizza(
    id: Int,
    nombre: String,
    precio: Int,
    tamano: Tamano? = null,
    ingredientes: Map<String, Int> = mapOf(nombre to 1),
) : Producto(id, if (tamano != null) "$nombre ${tamano.nombreVisible}" else nombre, Categoria.PIZZA, precio, tamano, ingredientes) {

    override fun detallarProducto(): String = "${super.detallarProducto()} · Ingredientes: ${ingredientes.keys.joinToString()}"
}

/** Combo: varios productos por un precio; sus ingredientes son la suma de los de sus componentes. */
@Promocionable(porcentaje = 15, descripcion = "15% en combos")
class Combo(
    id: Int,
    nombre: String,
    precio: Int,
    val componentes: Map<Producto, Int>,
) : Producto(id, nombre, Categoria.COMBO, precio, ingredientes = sumarIngredientes(componentes)) {

    override fun detallarProducto(): String =
        "${super.detallarProducto()} · Incluye: ${componentes.entries.joinToString { "${it.value} x ${it.key.nombre}" }}"
}

class Bebida(id: Int, nombre: String, precio: Int) : Producto(id, nombre, Categoria.BEBIDA, precio)

class Acompanamiento(id: Int, nombre: String, precio: Int) : Producto(id, nombre, Categoria.ACOMPANAMIENTO, precio)

private fun sumarIngredientes(componentes: Map<Producto, Int>): Map<String, Int> {
    require(componentes.isNotEmpty()) { "Un combo debe tener al menos un producto" }
    val total: MutableMap<String, Int> = mutableMapOf()
    for ((producto, unidades) in componentes) {
        for ((ingrediente, cantidad) in producto.ingredientes) {
            total[ingrediente] = (total[ingrediente] ?: 0) + cantidad * unidades
        }
    }
    return total
}
