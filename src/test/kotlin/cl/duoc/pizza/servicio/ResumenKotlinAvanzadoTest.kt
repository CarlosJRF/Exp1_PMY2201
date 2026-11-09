package cl.duoc.pizza.servicio

import kotlin.test.Test
import kotlin.test.assertContains

class ResumenKotlinAvanzadoTest {
    private val resumen: String = ResumenKotlinAvanzado.generar()

    @Test
    fun `incluye todas las secciones del paso 6`() {
        listOf(
            "1. VALIDACIONES (Regex y Ranges)",
            "2. PEDIDOS COMBINADOS (operador +)",
            "3. DESESTRUCTURACIÓN",
            "4. PRODUCTOS PROMOCIONABLES (@Promocionable)",
            "5. IGUALDAD DE OBJETOS (equals / hashCode)",
            "6. REFLECTION (propiedades y métodos)",
        ).forEach { assertContains(resumen, it) }
    }

    @Test
    fun `muestra los resultados esperados de cada concepto`() {
        assertContains(resumen, "\"9123456789\" → +56 (912) 345-6789")
        assertContains(resumen, "15-11-2026 → dentro del rango, aplica descuento")
        assertContains(resumen, "Promoción Noviembre Pizzero                  -\$5.170")
        assertContains(resumen, "cliente = Ana · total = \$48.404")
        assertContains(resumen, "Combo: @Promocionable(15%)")
        assertContains(resumen, "hashSetOf(Ana, ana, Luis).size = 2")
        assertContains(resumen, "Agregar #99 al catálogo → rechazado (ya existe)")
        assertContains(resumen, "operator fun plus(otro: Pedido): Pedido")
    }
}
