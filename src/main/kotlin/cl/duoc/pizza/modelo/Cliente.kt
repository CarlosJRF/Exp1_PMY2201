package cl.duoc.pizza.modelo

import cl.duoc.pizza.util.Seguridad

/** Cliente registrado: inicia sesión y hace pedidos que se despachan a su dirección. */
class Cliente(
    id: Int,
    nombre: String,
    email: String,
    /** Hash Argon2id en formato PHC (ver [Seguridad]); nunca la contraseña original. */
    val passwordHash: String,
    telefono: String,
    val direccion: String,
) : Usuario(id, nombre, telefono, email) {

    /** Email al que se envían notificaciones, o null si se registró con el valor predeterminado. */
    val emailNotificaciones: String?
        get() = if (email == ContactoPredeterminado.EMAIL) null else email

    override fun descripcion(): String = "Cliente: ${super.descripcion()} · $direccion"

    override fun validarPassword(password: String): Boolean = Seguridad.verificarPassword(password, passwordHash)
}
