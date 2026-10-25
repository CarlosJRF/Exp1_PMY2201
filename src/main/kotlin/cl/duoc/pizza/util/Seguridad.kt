package cl.duoc.pizza.util

import java.security.MessageDigest

object Seguridad {
    /** Devuelve el hash SHA-256 del texto en hexadecimal. Nunca se guarda la contraseña original. */
    fun hashSha256(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
