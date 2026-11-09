package cl.duoc.pizza.servicio

/** Validaciones de datos ingresados por el usuario, con expresiones regulares (Regex) y rangos (Ranges). */
object Validaciones {

    /** Formato estándar nombre@dominio.com: parte local, @, dominio y una extensión de al menos 2 letras. */
    private val FORMATO_EMAIL: Regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    /** Caracteres que puede traer un teléfono escrito a mano: +, dígitos, espacios, guiones, puntos y paréntesis. */
    private val CARACTERES_TELEFONO: Regex = Regex("^\\+?[0-9 ().-]+$")

    /** 12 dígitos separados en grupos: código de país (2), área (3), bloque (3) y bloque (4). */
    private val GRUPOS_TELEFONO: Regex = Regex("^(\\d{2})(\\d{3})(\\d{3})(\\d{4})$")

    /** Código de país que se agrega cuando el teléfono trae solo los 10 dígitos nacionales. */
    const val CODIGO_PAIS_PREDETERMINADO = "56"

    /** Unidades permitidas por ingreso (pizzas, bebidas, etc.). */
    val CANTIDAD_PERMITIDA: IntRange = 1..100

    const val MENSAJE_EMAIL = "El correo debe tener el formato nombre@dominio.com (por ejemplo, ana.perez@duoc.cl)"
    const val MENSAJE_TELEFONO =
        "El teléfono debe tener 10 dígitos, o 12 con código de país (por ejemplo, 56 912 345 6789)"

    fun esEmailValido(email: String): Boolean = FORMATO_EMAIL.matches(email.trim())

    /**
     * Devuelve el teléfono con formato uniforme `+XX (XXX) XXX-XXXX`, o null si no se puede formatear.
     * Acepta espacios, guiones, puntos y paréntesis; con 10 dígitos agrega el código de país [CODIGO_PAIS_PREDETERMINADO].
     */
    fun formatearTelefono(telefono: String): String? {
        val texto: String = telefono.trim()
        if (!CARACTERES_TELEFONO.matches(texto)) return null
        val digitos: String = texto.replace(Regex("\\D"), "")
        val completo: String = when (digitos.length) {
            10 -> CODIGO_PAIS_PREDETERMINADO + digitos
            12 -> digitos
            else -> return null
        }
        return GRUPOS_TELEFONO.replace(completo, "+$1 ($2) $3-$4")
    }

    fun esCantidadValida(cantidad: Int): Boolean = cantidad in CANTIDAD_PERMITIDA

    fun validarCantidad(cantidad: Int) {
        if (!esCantidadValida(cantidad)) {
            throw ErrorNegocio(
                "La cantidad debe estar entre ${CANTIDAD_PERMITIDA.first} y ${CANTIDAD_PERMITIDA.last} (ingresaste $cantidad)",
            )
        }
    }
}
