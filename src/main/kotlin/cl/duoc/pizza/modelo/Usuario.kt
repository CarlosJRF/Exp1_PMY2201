package cl.duoc.pizza.modelo

/** Persona del sistema con sus datos de contacto. Se especializa en Cliente, Repartidor y Administrador. */
open class Usuario(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val email: String,
) {
    /** Descripción para listados; cada subclase agrega lo suyo. */
    open fun descripcion(): String = "$nombre · ${telefono.ifBlank { "sin teléfono" }} · $email"

    /** Por defecto un usuario no puede iniciar sesión; lo permiten Cliente y Administrador. */
    open fun validarPassword(password: String): Boolean = false
}
