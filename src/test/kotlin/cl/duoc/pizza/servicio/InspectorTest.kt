package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Bebida
import cl.duoc.pizza.modelo.Cliente
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Pizza
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class InspectorTest {

    @Test
    fun `lista propiedades y metodos de Pedido`() {
        val descripcion = Inspector.describir(Pedido::class)
        assertContains(descripcion, "Clase Pedido")
        assertContains(descripcion, "val cantidades: Map<Producto, Int>")
        assertContains(descripcion, "var notas: String?")
        assertContains(descripcion, "fun cambiarEstado(nuevo: EstadoPedido): Unit")
        assertContains(descripcion, "private val _cantidades: MutableMap<Producto, Int>")
    }

    @Test
    fun `muestra la anotacion promocionable de la clase`() {
        assertContains(Inspector.describir(Pizza::class), "Clase Pizza · @Promocionable(10%, \"10% en pizzas\")")
        assertFalse(Inspector.describir(Bebida::class).contains("@Promocionable"))
        assertContains(Inspector.describir(Cliente::class), "fun validarPassword(password: String): Boolean")
    }
}
