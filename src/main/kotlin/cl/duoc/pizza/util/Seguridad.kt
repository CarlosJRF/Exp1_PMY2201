package cl.duoc.pizza.util

import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Hash de contraseñas con Argon2id (Bouncy Castle). Nunca se guarda la contraseña original:
 * se guarda un texto en formato PHC con el algoritmo, sus parámetros, la sal y el hash, por ejemplo
 * `$argon2id$v=19$m=19456,t=2,p=1$<sal en Base64>$<hash en Base64>`.
 */
object Seguridad {
    /** Memoria en KiB (19 MiB), iteraciones y paralelismo: configuración mínima recomendada por OWASP. */
    const val MEMORIA_KIB = 19_456
    const val ITERACIONES = 2
    const val PARALELISMO = 1

    private const val LARGO_SAL = 16
    private const val LARGO_HASH = 32
    private const val PREFIJO = "argon2id"

    private val aleatorio: SecureRandom = SecureRandom()
    private val base64: Base64.Encoder = Base64.getEncoder().withoutPadding()

    /** Genera una sal aleatoria nueva y devuelve el hash Argon2id de la contraseña en formato PHC. */
    fun hashPassword(password: String): String {
        val sal: ByteArray = ByteArray(LARGO_SAL).also { aleatorio.nextBytes(it) }
        val hash: ByteArray = argon2id(password, sal, MEMORIA_KIB, ITERACIONES, PARALELISMO, LARGO_HASH)
        return "\$$PREFIJO\$v=19\$m=$MEMORIA_KIB,t=$ITERACIONES,p=$PARALELISMO\$${base64.encodeToString(sal)}\$${base64.encodeToString(hash)}"
    }

    /**
     * Recalcula el hash con la sal y los parámetros guardados y lo compara en tiempo constante.
     * Si el texto guardado no tiene el formato esperado, devuelve false.
     */
    fun verificarPassword(password: String, hashGuardado: String): Boolean {
        // "$argon2id$v=19$m=..,t=..,p=..$sal$hash" -> ["", "argon2id", "v=19", "m=..,t=..,p=..", sal, hash]
        val partes: List<String> = hashGuardado.split("$")
        if (partes.size != 6 || partes[1] != PREFIJO || partes[2] != "v=19") return false
        return try {
            val parametros: Map<String, Int> = partes[3].split(",").associate { parametro ->
                val nombreYValor: List<String> = parametro.split("=")
                require(nombreYValor.size == 2) { "Parámetro mal formado: $parametro" }
                nombreYValor[0] to nombreYValor[1].toInt()
            }
            val sal: ByteArray = Base64.getDecoder().decode(partes[4])
            val esperado: ByteArray = Base64.getDecoder().decode(partes[5])
            val calculado: ByteArray = argon2id(
                password, sal, parametros.getValue("m"), parametros.getValue("t"), parametros.getValue("p"), esperado.size,
            )
            MessageDigest.isEqual(esperado, calculado)
        } catch (e: IllegalArgumentException) {
            false // número, Base64 o parámetro mal formado (NumberFormatException también es IllegalArgumentException)
        } catch (e: NoSuchElementException) {
            false // falta m, t o p
        }
    }

    private fun argon2id(
        password: String,
        sal: ByteArray,
        memoriaKib: Int,
        iteraciones: Int,
        paralelismo: Int,
        largo: Int,
    ): ByteArray {
        val parametros: Argon2Parameters = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withVersion(Argon2Parameters.ARGON2_VERSION_13)
            .withSalt(sal)
            .withMemoryAsKB(memoriaKib)
            .withIterations(iteraciones)
            .withParallelism(paralelismo)
            .build()
        val generador: Argon2BytesGenerator = Argon2BytesGenerator()
        generador.init(parametros)

        val bytesPassword: ByteArray = password.toByteArray(Charsets.UTF_8)
        val hash: ByteArray = ByteArray(largo)
        try {
            generador.generateBytes(bytesPassword, hash)
        } finally {
            bytesPassword.fill(0) // no dejar la contraseña en memoria más tiempo del necesario
        }
        return hash
    }
}
