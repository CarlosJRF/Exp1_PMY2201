package cl.duoc.pizza.modelo

/** Administrador de la pizzería: gestiona inventario, pedidos y repartos. */
class Administrador(
    id: Int,
    nombre: String,
    email: String,
    private val passwordHash: String,
) : Usuario(id, nombre, telefono = "", email) {

    override fun descripcion(): String = "Administrador: ${super.descripcion()}"

    override fun validarPassword(passwordHash: String): Boolean = this.passwordHash == passwordHash
}
