package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Cliente
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.repositorio.UsuarioRepositorio
import cl.duoc.pizza.util.Seguridad

class UsuarioServicio(private val repositorio: UsuarioRepositorio) {

    companion object {
        const val LARGO_MINIMO_PASSWORD = 6
        const val EMAIL_PREDETERMINADO = Cliente.EMAIL_PREDETERMINADO
        const val TELEFONO_PREDETERMINADO = Cliente.TELEFONO_PREDETERMINADO
        private val FORMATO_EMAIL: Regex = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")
        private val HASH_DE_RELLENO: String = Seguridad.hashPassword("relleno-sin-usuario")
        private val FORMATO_TELEFONO: Regex = Regex("^\\+?\\d{8,12}$")
    }

    /** Email en minúsculas si el formato es válido; si no, [EMAIL_PREDETERMINADO]. */
    fun validarEmail(email: String): String {
        val normalizado: String = email.trim().lowercase()
        return if (FORMATO_EMAIL.matches(normalizado)) normalizado else EMAIL_PREDETERMINADO
    }

    /** Teléfono de 8 a 12 dígitos (con + opcional); si no, [TELEFONO_PREDETERMINADO]. */
    fun validarTelefono(telefono: String): String {
        val limpio: String = telefono.trim()
        return if (FORMATO_TELEFONO.matches(limpio)) limpio else TELEFONO_PREDETERMINADO
    }

    /**
     * Registra un cliente. Si el email o el teléfono no tienen un formato válido no se rechaza el registro:
     * se guardan los valores predeterminados y la UI avisa al usuario.
     */
    fun registrar(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        direccion: String,
    ): Cliente {
        val emailValidado: String = validarEmail(email)

        if (nombre.isBlank()) throw ErrorNegocio("El nombre es obligatorio")
        if (repositorio.buscarPorEmail(emailValidado) != null) {
            val mensaje: String = if (emailValidado == EMAIL_PREDETERMINADO) {
                "El email no tiene un formato válido y ya hay un usuario con $EMAIL_PREDETERMINADO; ingresa un email correcto"
            } else {
                "Ya existe un usuario con ese email"
            }
            throw ErrorNegocio(mensaje)
        }
        if (password.length < LARGO_MINIMO_PASSWORD) {
            throw ErrorNegocio("La contraseña debe tener al menos $LARGO_MINIMO_PASSWORD caracteres")
        }
        if (direccion.isBlank()) throw ErrorNegocio("La dirección es obligatoria")

        val cliente: Cliente = Cliente(
            id = repositorio.siguienteId(),
            nombre = nombre.trim(),
            email = emailValidado,
            passwordHash = Seguridad.hashPassword(password),
            telefono = validarTelefono(telefono),
            direccion = direccion.trim(),
        )
        repositorio.guardar(cliente)
        return cliente
    }

    fun listar(): List<Usuario> = repositorio.listar()

    fun iniciarSesion(email: String, password: String): Usuario {
        val usuario: Usuario? = repositorio.buscarPorEmail(email)
        if (usuario == null) {
            // Se verifica igual contra un hash de relleno para que un email inexistente tarde lo mismo
            // que una contraseña incorrecta y no se pueda saber qué emails están registrados.
            Seguridad.verificarPassword(password, HASH_DE_RELLENO)
            throw ErrorNegocio("Email o contraseña incorrectos")
        }
        if (!usuario.validarPassword(password)) {
            throw ErrorNegocio("Email o contraseña incorrectos")
        }
        return usuario
    }
}
