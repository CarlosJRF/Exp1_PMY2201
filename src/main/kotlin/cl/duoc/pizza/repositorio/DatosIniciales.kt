package cl.duoc.pizza.repositorio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Repartidor
import cl.duoc.pizza.modelo.Tamano
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.util.Seguridad

/** Datos precargados al iniciar la app (no hay persistencia todavía). */
object DatosIniciales {
    val admin: Usuario = Usuario(
        id = 1,
        nombre = "Administrador",
        email = "admin@duocpizza.cl",
        passwordHash = Seguridad.hashSha256("admin123"),
        telefono = "",
        direccion = "",
        esAdmin = true,
    )

    /** Receta base (tamaño personal), en porciones de cada ingrediente. */
    private val base: Map<String, Int> = mapOf("Masa" to 1, "Salsa de tomate" to 1, "Queso mozzarella" to 1)
    private val margarita: Map<String, Int> = base + mapOf("Tomate" to 1, "Albahaca" to 1)
    private val pepperoni: Map<String, Int> = base + mapOf("Pepperoni" to 1)
    private val napolitana: Map<String, Int> = base + mapOf("Tomate" to 1, "Aceitunas" to 1, "Orégano" to 1)
    private val hawaiana: Map<String, Int> = base + mapOf("Jamón" to 1, "Piña" to 1)

    private fun pizza(id: Int, nombre: String, precio: Int, tamano: Tamano, receta: Map<String, Int>): Producto =
        Producto(id, nombre, Categoria.PIZZA, precio, tamano, receta.mapValues { it.value * tamano.porciones })

    val productos: List<Producto> = listOf(
        pizza(1, "Margarita", 5990, Tamano.PERSONAL, margarita),
        pizza(2, "Margarita", 9990, Tamano.MEDIANA, margarita),
        pizza(3, "Margarita", 13990, Tamano.FAMILIAR, margarita),
        pizza(4, "Pepperoni", 6490, Tamano.PERSONAL, pepperoni),
        pizza(5, "Pepperoni", 10990, Tamano.MEDIANA, pepperoni),
        pizza(6, "Pepperoni", 14990, Tamano.FAMILIAR, pepperoni),
        pizza(7, "Napolitana", 6490, Tamano.PERSONAL, napolitana),
        pizza(8, "Napolitana", 10990, Tamano.MEDIANA, napolitana),
        pizza(9, "Napolitana", 14990, Tamano.FAMILIAR, napolitana),
        pizza(10, "Hawaiana", 6990, Tamano.PERSONAL, hawaiana),
        pizza(11, "Hawaiana", 11490, Tamano.MEDIANA, hawaiana),
        pizza(12, "Hawaiana", 15490, Tamano.FAMILIAR, hawaiana),
        Producto(13, "Coca-Cola 1.5L", Categoria.BEBIDA, 2490),
        Producto(14, "Sprite 1.5L", Categoria.BEBIDA, 2490),
        Producto(15, "Agua mineral 500ml", Categoria.BEBIDA, 1290),
        Producto(16, "Palitos de ajo", Categoria.ACOMPANAMIENTO, 3490),
        Producto(17, "Papas fritas", Categoria.ACOMPANAMIENTO, 2990),
    )

    val repartidores: List<Repartidor> = listOf(
        Repartidor(1, "Pedro Soto", "+56911111111", 2.5),
        Repartidor(2, "Camila Rojas", "+56922222222", 0.8),
        Repartidor(3, "Diego Muñoz", "+56933333333", 4.1),
    )

    /** Inventario inicial: nombre del ingrediente -> cantidad disponible. */
    val ingredientes: Map<String, Int> = mapOf(
        "Masa" to 60,
        "Salsa de tomate" to 60,
        "Queso mozzarella" to 60,
        "Tomate" to 30,
        "Albahaca" to 20,
        "Pepperoni" to 20,
        "Aceitunas" to 20,
        "Orégano" to 20,
        "Jamón" to 20,
        "Piña" to 20,
        "Coca-Cola 1.5L" to 20,
        "Sprite 1.5L" to 20,
        "Agua mineral 500ml" to 20,
        "Palitos de ajo" to 15,
        "Papas fritas" to 15,
    )
}
