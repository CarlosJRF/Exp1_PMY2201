package cl.duoc.pizza.modelo

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String,
    val direccion: String,
    val esAdmin: Boolean = false,
)
