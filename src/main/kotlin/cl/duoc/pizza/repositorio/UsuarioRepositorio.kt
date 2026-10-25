package cl.duoc.pizza.repositorio

import cl.duoc.pizza.modelo.Usuario

class UsuarioRepositorio(usuariosIniciales: List<Usuario> = emptyList()) {
    private val usuarios = mutableMapOf<Int, Usuario>()
    private var ultimoId = 0

    init {
        usuariosIniciales.forEach { guardar(it) }
    }

    fun siguienteId(): Int = ++ultimoId

    fun guardar(usuario: Usuario) {
        usuarios[usuario.id] = usuario
        ultimoId = maxOf(ultimoId, usuario.id)
    }

    /** La búsqueda por email no distingue mayúsculas de minúsculas. */
    fun buscarPorEmail(email: String): Usuario? =
        usuarios.values.find { it.email.equals(email.trim(), ignoreCase = true) }

    fun listar(): List<Usuario> = usuarios.values.toList()
}
