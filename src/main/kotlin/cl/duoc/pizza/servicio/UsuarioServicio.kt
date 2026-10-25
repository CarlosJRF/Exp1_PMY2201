package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.repositorio.UsuarioRepositorio
import cl.duoc.pizza.util.Seguridad

class UsuarioServicio(private val repositorio: UsuarioRepositorio) {

    companion object {
        const val LARGO_MINIMO_PASSWORD = 6
        private val FORMATO_EMAIL = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")
        private val FORMATO_TELEFONO = Regex("^\\+?\\d{8,12}$")
    }

    fun registrar(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        direccion: String,
    ): Usuario {
        val emailNormalizado = email.trim().lowercase()

        if (nombre.isBlank()) throw ErrorNegocio("El nombre es obligatorio")
        if (!FORMATO_EMAIL.matches(emailNormalizado)) throw ErrorNegocio("El email no tiene un formato válido")
        if (repositorio.buscarPorEmail(emailNormalizado) != null) {
            throw ErrorNegocio("Ya existe un usuario con ese email")
        }
        if (password.length < LARGO_MINIMO_PASSWORD) {
            throw ErrorNegocio("La contraseña debe tener al menos $LARGO_MINIMO_PASSWORD caracteres")
        }
        if (!FORMATO_TELEFONO.matches(telefono.trim())) {
            throw ErrorNegocio("El teléfono debe tener entre 8 y 12 dígitos")
        }
        if (direccion.isBlank()) throw ErrorNegocio("La dirección es obligatoria")

        val usuario = Usuario(
            id = repositorio.siguienteId(),
            nombre = nombre.trim(),
            email = emailNormalizado,
            passwordHash = Seguridad.hashSha256(password),
            telefono = telefono.trim(),
            direccion = direccion.trim(),
        )
        repositorio.guardar(usuario)
        return usuario
    }

    fun iniciarSesion(email: String, password: String): Usuario {
        val usuario = repositorio.buscarPorEmail(email)
        if (usuario == null || usuario.passwordHash != Seguridad.hashSha256(password)) {
            throw ErrorNegocio("Email o contraseña incorrectos")
        }
        return usuario
    }
}
