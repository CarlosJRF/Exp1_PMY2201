package cl.duoc.pizza.servicio

/** Validaciones de datos ingresados por el usuario, con expresiones regulares (Regex) y rangos (Ranges). */
object Validaciones {

    /** Formato estándar nombre@dominio.com: parte local, @, dominio y una extensión de al menos 2 letras. */
    private val FORMATO_EMAIL: Regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    /** Caracteres que puede traer un teléfono escrito a mano: +, dígitos, espacios, guiones, puntos y paréntesis. */
    private val CARACTERES_TELEFONO: Regex = Regex("^\\+?[0-9 ().-]+$")

    /** 12 dígitos separados en grupos: código de país (2), área (3), bloque (3) y bloque (4). */
    private val GRUPOS_TELEFONO: Regex = Regex("^(\\d{2})(\\d{3})(\\d{3})(\\d{4})$")

    /** Número chileno de 11 dígitos: código de país 56, primer dígito (9 en celulares) y dos bloques de 4. */
    private val GRUPOS_TELEFONO_CHILE: Regex = Regex("^(56)(\\d)(\\d{4})(\\d{4})$")

    /** Código de país que se agrega cuando el teléfono trae solo los dígitos nacionales (9 o 10). */
    const val CODIGO_PAIS_PREDETERMINADO = "56"

    /** Unidades permitidas por ingreso (pizzas, bebidas, etc.). */
    val CANTIDAD_PERMITIDA: IntRange = 1..100

    const val MENSAJE_EMAIL = "El correo debe tener el formato nombre@dominio.com (por ejemplo, ana.perez@duoc.cl)"
    const val MENSAJE_TELEFONO =
        "El teléfono debe tener 9 dígitos (celular chileno, por ejemplo 9 1234 5678), 11 si incluye el 56, " +
            "o 10/12 en formato internacional (por ejemplo, 56 912 345 6789)"

    fun esEmailValido(email: String): Boolean = FORMATO_EMAIL.matches(email.trim())

    /**
     * Devuelve el teléfono con formato uniforme, o null si no se puede formatear:
     * - 9 dígitos (celular chileno) o 11 que empiezan con 56 → `+56 9 1234-5678`.
     * - 10 dígitos (agrega [CODIGO_PAIS_PREDETERMINADO]) o 12 con código de país → `+XX (XXX) XXX-XXXX`.
     * Acepta espacios, guiones, puntos y paréntesis.
     */
    fun formatearTelefono(telefono: String): String? {
        val texto: String = telefono.trim()
        if (!CARACTERES_TELEFONO.matches(texto)) return null
        val digitos: String = texto.replace(Regex("\\D"), "")
        return when (digitos.length) {
            9 -> GRUPOS_TELEFONO_CHILE.replace(CODIGO_PAIS_PREDETERMINADO + digitos, "+$1 $2 $3-$4")
            11 -> if (GRUPOS_TELEFONO_CHILE.matches(digitos)) GRUPOS_TELEFONO_CHILE.replace(digitos, "+$1 $2 $3-$4") else null
            10 -> GRUPOS_TELEFONO.replace(CODIGO_PAIS_PREDETERMINADO + digitos, "+$1 ($2) $3-$4")
            12 -> GRUPOS_TELEFONO.replace(digitos, "+$1 ($2) $3-$4")
            else -> null
        }
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
