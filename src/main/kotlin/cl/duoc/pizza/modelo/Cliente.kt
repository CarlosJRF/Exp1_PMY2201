package cl.duoc.pizza.modelo

import cl.duoc.pizza.util.Seguridad
import java.util.Objects

/**
 * Cliente registrado: inicia sesión y hace pedidos que se despachan a su dirección.
 * Dos clientes son iguales si tienen el mismo nombre y email (sin distinguir mayúsculas), aunque su id sea distinto.
 */
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

    /** Desestructuración: `val (nombre, email, telefono) = cliente`. */
    operator fun component1(): String = nombre
    operator fun component2(): String = email
    operator fun component3(): String = telefono

    override fun equals(other: Any?): Boolean =
        other is Cliente &&
            nombre.trim().equals(other.nombre.trim(), ignoreCase = true) &&
            email.trim().equals(other.email.trim(), ignoreCase = true)

    /** Coherente con [equals]: los mismos atributos clave, normalizados igual. */
    override fun hashCode(): Int = Objects.hash(nombre.trim().lowercase(), email.trim().lowercase())
}
