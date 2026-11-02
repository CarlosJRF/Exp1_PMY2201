package cl.duoc.pizza.ui

import cl.duoc.pizza.servicio.ErrorNegocio
import java.io.Console

/** Se lanza cuando la entrada estándar se cierra (por ejemplo, Ctrl+D o fin de un archivo). */
class EntradaFinalizada : Exception()

/** Lectura y escritura por consola. Es la única clase que usa readln/println junto a los menús. */
class Consola {

    fun titulo(texto: String) {
        println()
        println("=== $texto ===")
    }

    fun mensaje(texto: String) = println(texto)

    fun exito(texto: String) = println("✔ $texto")

    fun error(texto: String) = println("✖ $texto")

    fun leerTexto(etiqueta: String): String {
        while (true) {
            print("$etiqueta: ")
            val texto: String = leerLinea().trim()
            if (texto.isNotEmpty()) return texto
            error("Este campo no puede estar vacío")
        }
    }

    /** Oculta lo que se escribe cuando hay una terminal real; con ./gradlew run se muestra. */
    fun leerPassword(etiqueta: String): String {
        val terminal: Console = System.console() ?: return leerTexto(etiqueta)
        while (true) {
            val password: String = terminal.readPassword("$etiqueta: ")?.let { String(it) } ?: throw EntradaFinalizada()
            if (password.isNotEmpty()) return password
            error("Este campo no puede estar vacío")
        }
    }

    /** Lee un número; si el texto no es numérico o está fuera de rango, explica el error y vuelve a pedirlo. */
    fun leerEntero(etiqueta: String, minimo: Int, maximo: Int): Int {
        while (true) {
            print("$etiqueta: ")
            val texto: String = leerLinea().trim()
            try {
                val numero: Int = texto.toInt()
                if (numero in minimo..maximo) return numero
                error("$numero está fuera de rango: ingresa un número entre $minimo y $maximo")
            } catch (e: NumberFormatException) {
                error("\"$texto\" no es un número válido: ingresa un número entre $minimo y $maximo")
            }
        }
    }

    /** Lee un texto opcional: si se deja vacío devuelve null. */
    fun leerOpcional(etiqueta: String): String? {
        print("$etiqueta: ")
        return leerLinea().trim().ifBlank { null }
    }

    fun confirmar(pregunta: String): Boolean {
        while (true) {
            print("$pregunta (s/n): ")
            when (leerLinea().trim().lowercase()) {
                "s", "si", "sí" -> return true
                "n", "no" -> return false
                else -> error("Responde s o n")
            }
        }
    }

    /** Muestra las opciones numeradas desde 1, con 0 como opción de salida, y devuelve la elegida. */
    fun menu(titulo: String, opciones: List<String>, opcionSalir: String): Int {
        titulo(titulo)
        opciones.forEachIndexed { indice, opcion -> println("${indice + 1}. $opcion") }
        println("0. $opcionSalir")
        return leerEntero("Opción", 0, opciones.size)
    }

    /** Ejecuta una acción y muestra los errores de negocio o de datos sin detener la app. */
    fun intentar(accion: () -> Unit) {
        try {
            accion()
        } catch (e: ErrorNegocio) {
            error(e.message ?: "Error desconocido")
        } catch (e: IllegalArgumentException) {
            error("Dato no válido: ${e.message ?: "revisa lo ingresado"}")
        }
    }

    private fun leerLinea(): String = readlnOrNull() ?: throw EntradaFinalizada()
}
