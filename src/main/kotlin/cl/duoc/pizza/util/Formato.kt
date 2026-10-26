package cl.duoc.pizza.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Formato {
    private val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    /** Formatea un monto en pesos chilenos: 12990 -> "$12.990". */
    fun precio(monto: Int): String {
        val signo: String = if (monto < 0) "-" else ""
        val miles: String = kotlin.math.abs(monto).toString().reversed().chunked(3).joinToString(".").reversed()
        return "$signo$$miles"
    }

    fun fecha(fecha: LocalDateTime): String = fecha.format(FORMATO_FECHA)
}
