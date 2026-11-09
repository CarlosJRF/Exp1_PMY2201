package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Promocionable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

/** Analiza clases en tiempo de ejecución con reflection: anotaciones, propiedades y métodos. */
object Inspector {

    /** Texto con la anotación @Promocionable (si tiene), las propiedades y los métodos de [clase]. */
    fun describir(clase: KClass<*>): String = buildString {
        val promocion: Promocionable? = clase.findAnnotation<Promocionable>()
        appendLine("Clase ${clase.simpleName}" + (promocion?.let { " · @Promocionable(${it.porcentaje}%, \"${it.descripcion}\")" } ?: ""))
        appendLine("  Propiedades:")
        for (propiedad in clase.memberProperties.sortedBy { it.name }) {
            val tipoDeclaracion: String = if (propiedad is KMutableProperty1<*, *>) "var" else "val"
            appendLine("    ${visibilidad(propiedad.visibility)}$tipoDeclaracion ${propiedad.name}: ${tipo(propiedad.returnType)}")
        }
        appendLine("  Métodos:")
        for (funcion in clase.memberFunctions.sortedBy { it.name }) {
            // El primer parámetro es el propio objeto (receptor); se omite.
            val parametros: String = funcion.parameters.drop(1).joinToString { "${it.name}: ${tipo(it.type)}" }
            val operador: String = if (funcion.isOperator) "operator " else ""
            appendLine("    ${visibilidad(funcion.visibility)}${operador}fun ${funcion.name}($parametros): ${tipo(funcion.returnType)}")
        }
    }.trimEnd()

    private fun visibilidad(visibilidad: KVisibility?): String =
        if (visibilidad == null || visibilidad == KVisibility.PUBLIC) "" else "${visibilidad.name.lowercase()} "

    /** Nombre de tipo legible: sin los prefijos de paquete. */
    private fun tipo(tipo: KType): String = tipo.toString().replace(Regex("\\b[a-z]+(\\.[a-z]+)*\\.(?=[A-Z])"), "")
}
