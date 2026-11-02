package cl.duoc.pizza.modelo

import cl.duoc.pizza.util.Seguridad

/** Administrador de la pizzería: gestiona inventario, pedidos y repartos. */
class Administrador(
    id: Int,
    nombre: String,
    email: String,
    private val passwordHash: String,
) : Usuario(id, nombre, telefono = "", email) {

    override fun descripcion(): String = "Administrador: ${super.descripcion()}"

    override fun validarPassword(password: String): Boolean = Seguridad.verificarPassword(password, passwordHash)
}
