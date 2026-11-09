package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Cliente
import cl.duoc.pizza.modelo.ContactoPredeterminado
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.repositorio.UsuarioRepositorio
import cl.duoc.pizza.util.Seguridad

class UsuarioServicio(private val repositorio: UsuarioRepositorio) {

    companion object {
        const val LARGO_MINIMO_PASSWORD = 6
        private val HASH_DE_RELLENO: String = Seguridad.hashPassword("relleno-sin-usuario")
    }

    /** Email en minúsculas si el formato es válido; si no, [ContactoPredeterminado.EMAIL]. */
    fun validarEmail(email: String): String {
        val normalizado: String = email.trim().lowercase()
        return if (Validaciones.esEmailValido(normalizado)) normalizado else ContactoPredeterminado.EMAIL
    }

    /** Teléfono con formato `+XX (XXX) XXX-XXXX`; si no se puede formatear, [ContactoPredeterminado.TELEFONO]. */
    fun validarTelefono(telefono: String): String =
        Validaciones.formatearTelefono(telefono) ?: ContactoPredeterminado.TELEFONO

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
        val emailOcupado: Boolean = repositorio.buscarPorEmail(emailValidado) != null
        if (emailOcupado && emailValidado == ContactoPredeterminado.EMAIL) {
            throw ErrorNegocio(
                "El email no tiene un formato válido y ya hay un usuario con ${ContactoPredeterminado.EMAIL}; ingresa un email correcto",
            )
        }
        // Cliente provisorio solo para comparar: equals usa nombre y email (ver Cliente.equals).
        val candidato: Cliente = Cliente(0, nombre.trim(), emailValidado, passwordHash = "", telefono = "", direccion = "")
        if (candidato in repositorio.listar()) {
            throw ErrorNegocio("Ya existe un cliente registrado con el nombre ${candidato.nombre} y el email ${candidato.email}")
        }
        // Otra persona con el mismo email: no se permite porque el email es el usuario para iniciar sesión.
        if (emailOcupado) throw ErrorNegocio("Ya existe un usuario con ese email")
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
