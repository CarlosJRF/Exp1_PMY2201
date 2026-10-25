package cl.duoc.pizza.repositorio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Tamano
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.util.Seguridad

/** Datos precargados al iniciar la app (no hay persistencia todavía). */
object DatosIniciales {
    val admin = Usuario(
        id = 1,
        nombre = "Administrador",
        email = "admin@duocpizza.cl",
        passwordHash = Seguridad.hashSha256("admin123"),
        telefono = "",
        direccion = "",
        esAdmin = true,
    )

    val productos: List<Producto> = listOf(
        Producto(1, "Margarita", Categoria.PIZZA, 5990, Tamano.PERSONAL),
        Producto(2, "Margarita", Categoria.PIZZA, 9990, Tamano.MEDIANA),
        Producto(3, "Margarita", Categoria.PIZZA, 13990, Tamano.FAMILIAR),
        Producto(4, "Pepperoni", Categoria.PIZZA, 6490, Tamano.PERSONAL),
        Producto(5, "Pepperoni", Categoria.PIZZA, 10990, Tamano.MEDIANA),
        Producto(6, "Pepperoni", Categoria.PIZZA, 14990, Tamano.FAMILIAR),
        Producto(7, "Napolitana", Categoria.PIZZA, 6490, Tamano.PERSONAL),
        Producto(8, "Napolitana", Categoria.PIZZA, 10990, Tamano.MEDIANA),
        Producto(9, "Napolitana", Categoria.PIZZA, 14990, Tamano.FAMILIAR),
        Producto(10, "Hawaiana", Categoria.PIZZA, 6990, Tamano.PERSONAL),
        Producto(11, "Hawaiana", Categoria.PIZZA, 11490, Tamano.MEDIANA),
        Producto(12, "Hawaiana", Categoria.PIZZA, 15490, Tamano.FAMILIAR),
        Producto(13, "Coca-Cola 1.5L", Categoria.BEBIDA, 2490),
        Producto(14, "Sprite 1.5L", Categoria.BEBIDA, 2490),
        Producto(15, "Agua mineral 500ml", Categoria.BEBIDA, 1290),
        Producto(16, "Palitos de ajo", Categoria.ACOMPANAMIENTO, 3490),
        Producto(17, "Papas fritas", Categoria.ACOMPANAMIENTO, 2990),
    )

    val stock: Map<Int, Int> = productos.associate { producto ->
        val unidades = when (producto.categoria) {
            Categoria.PIZZA -> 10
            Categoria.BEBIDA -> 20
            Categoria.ACOMPANAMIENTO -> 15
        }
        producto.id to unidades
    }
}
