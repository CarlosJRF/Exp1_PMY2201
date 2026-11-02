package cl.duoc.pizza.modelo

/** Cliente registrado: inicia sesión y hace pedidos que se despachan a su dirección. */
class Cliente(
    id: Int,
    nombre: String,
    email: String,
    val passwordHash: String,
    telefono: String,
    val direccion: String,
) : Usuario(id, nombre, telefono, email) {

    override fun descripcion(): String = "Cliente: ${super.descripcion()} · $direccion"

    override fun validarPassword(passwordHash: String): Boolean = this.passwordHash == passwordHash
}
