package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Acompanamiento
import cl.duoc.pizza.modelo.Bebida
import cl.duoc.pizza.modelo.Cliente
import cl.duoc.pizza.modelo.Combo
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Pizza
import cl.duoc.pizza.modelo.Producto
import cl.duoc.pizza.modelo.Promocionable
import cl.duoc.pizza.modelo.Promociones
import cl.duoc.pizza.modelo.Tamano
import cl.duoc.pizza.repositorio.DatosIniciales
import cl.duoc.pizza.repositorio.ProductoRepositorio
import cl.duoc.pizza.repositorio.UsuarioRepositorio
import cl.duoc.pizza.util.Formato
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Resumen que muestra en conjunto los conceptos avanzados de Kotlin aplicados en la pizzería:
 * validaciones (Regex y Ranges), operador +, desestructuración, anotaciones, igualdad de objetos y reflection.
 * Usa sus propios datos de demostración, así que no modifica los clientes ni pedidos de la sesión.
 */
object ResumenKotlinAvanzado {
    private const val ANCHO = 52

    /** Fecha de los pedidos de demostración: dentro del periodo de promoción, para que se vea el descuento. */
    private val FECHA_DEMO: LocalDateTime = LocalDateTime.of(2026, 11, 15, 20, 0)

    fun generar(): String = buildString {
        appendLine("=".repeat(ANCHO))
        appendLine("RESUMEN DE KOTLIN AVANZADO · DuocPizza")
        appendLine("=".repeat(ANCHO))
        validaciones()
        val combinado: Pedido = pedidosCombinados()
        desestructuracion(combinado)
        promocionables()
        igualdad()
        reflection()
    }

    private fun StringBuilder.seccion(titulo: String) {
        appendLine()
        appendLine(titulo)
        appendLine("-".repeat(ANCHO))
    }

    private fun StringBuilder.validaciones() {
        seccion("1. VALIDACIONES (Regex y Ranges)")
        appendLine("Correos (formato nombre@dominio.com):")
        for (email in listOf("ana.perez@duoc.cl", "ana.perez@duoc", "ana perez@duoc.cl")) {
            appendLine(if (Validaciones.esEmailValido(email)) "  ✔ $email" else "  ✖ $email → se pide reingresarlo")
        }
        appendLine("Teléfonos (+56 9 XXXX-XXXX o +XX (XXX) XXX-XXXX):")
        for (telefono in listOf("9 1234 5678", "+56 9 8765 4321", "9123456789", "12345")) {
            val resultado: String = Validaciones.formatearTelefono(telefono) ?: "✖ no válido, se pide reingresarlo"
            appendLine("  \"$telefono\" → $resultado")
        }
        val periodo: ClosedRange<LocalDate> = Promociones.PERIODO
        appendLine("Fechas (${Promociones.NOMBRE}: ${Formato.dia(periodo.start)}..${Formato.dia(periodo.endInclusive)}):")
        for (fecha in listOf(LocalDate.of(2026, 10, 31), LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 30))) {
            val estado: String = if (fecha in periodo) "dentro del rango, aplica descuento" else "fuera del rango"
            appendLine("  ${Formato.dia(fecha)} → $estado")
        }
        val rango: IntRange = Validaciones.CANTIDAD_PERMITIDA
        appendLine("Cantidades (rango ${rango.first}..${rango.last}):")
        appendLine("  " + listOf(0, 1, 100, 101).joinToString("  ") { if (it in rango) "$it ✔" else "$it ✖" })
    }

    private fun StringBuilder.pedidosCombinados(): Pedido {
        seccion("2. PEDIDOS COMBINADOS (operador +)")
        val primero: Pedido = Pedido(1, ANA, FECHA_DEMO).apply {
            agregar(producto(2), 2) // Margarita Mediana
            agregar(producto(13), 1) // Coca-Cola 1.5L
        }
        val segundo: Pedido = Pedido(2, ANA, FECHA_DEMO).apply {
            agregar(producto(5), 2) // Pepperoni Mediana
            agregar(producto(18), 1) // Combo Personal
        }
        appendLine("Pedido #1: ${detalle(primero)} · total ${Formato.precio(primero.total)}")
        appendLine("Pedido #2: ${detalle(segundo)} · total ${Formato.precio(segundo.total)}")
        val combinado: Pedido = primero + segundo
        appendLine("Pedido #1 + Pedido #2 (4 pizzas: aplica 10% y la promoción):")
        appendLine(ResumenPedido.generar(combinado))
        return combinado
    }

    private fun StringBuilder.desestructuracion(pedido: Pedido) {
        seccion("3. DESESTRUCTURACIÓN")
        val (nombre: String, correo: String, telefono: String) = ANA
        appendLine("val (nombre, correo, telefono) = cliente")
        appendLine("  nombre = $nombre · correo = $correo")
        appendLine("  telefono = $telefono")
        val (cliente: Cliente, productos: Map<Producto, Int>, total: Int) = pedido
        appendLine("val (cliente, productos, total) = pedido combinado")
        appendLine("  cliente = ${cliente.nombre} · total = ${Formato.precio(total)}")
        appendLine("  productos = ${productos.entries.joinToString { "${it.value} x ${it.key.nombre}" }}")
    }

    private fun StringBuilder.promocionables() {
        seccion("4. PRODUCTOS PROMOCIONABLES (@Promocionable)")
        appendLine("Clases revisadas con reflection:")
        val clases: List<KClass<out Producto>> = listOf(Pizza::class, Combo::class, Bebida::class, Acompanamiento::class)
        for (clase in clases) {
            val promocion: Promocionable? = clase.findAnnotation<Promocionable>()
            appendLine("  ${clase.simpleName}: ${promocion?.let { "@Promocionable(${it.porcentaje}%)" } ?: "sin promoción"}")
        }
        val elegibles: List<Producto> = DatosIniciales.productos.filter { it.esPromocionable }
        appendLine("Productos del catálogo elegibles (${elegibles.size} de ${DatosIniciales.productos.size}):")
        elegibles.chunked(2).forEach { par ->
            appendLine("  " + par.joinToString("  ") { "${it.nombre} (${it.promocion?.porcentaje}%)" })
        }
    }

    private fun StringBuilder.igualdad() {
        seccion("5. IGUALDAD DE OBJETOS (equals / hashCode)")
        val otraAna: Cliente = Cliente(2, "ana", "ANA@duoc.cl", "-", "+56 (900) 000-0000", "Otra dirección")
        val luis: Cliente = Cliente(3, "Luis", "luis@duoc.cl", "-", "+56 (987) 654-3210", "Los Aromos 45")
        appendLine("Clientes (nombre + email):")
        appendLine("  Ana #1 == ana #2 (otro teléfono): ${ANA == otraAna}")
        appendLine("  mismo hashCode: ${ANA.hashCode() == otraAna.hashCode()}")
        appendLine("  Ana == Luis: ${ANA == luis}")
        appendLine("  hashSetOf(Ana, ana, Luis).size = ${hashSetOf(ANA, otraAna, luis).size}")
        val usuarios: UsuarioServicio = UsuarioServicio(UsuarioRepositorio())
        usuarios.registrar("Ana Pérez", "ana@duoc.cl", "secreto", "9123456789", "Av. Siempre Viva 123")
        val duplicado: String = try {
            usuarios.registrar("ANA PÉREZ", "Ana@Duoc.cl", "secreto", "9123456789", "Av. Siempre Viva 123")
            "registrado (no debería)"
        } catch (e: ErrorNegocio) {
            "rechazado: ${e.message}"
        }
        appendLine("  Registrar de nuevo a Ana Pérez → $duplicado")

        val margarita: Producto = producto(2)
        val otraMargarita: Producto = Pizza(99, "Margarita", 10990, Tamano.MEDIANA)
        appendLine("Productos (nombre + categoría):")
        appendLine("  ${margarita.nombre} #2 == #99 (otro precio): ${margarita == otraMargarita}")
        appendLine("  Margarita Mediana == Familiar: ${margarita == producto(3)}")
        appendLine("  setOf(#2, #99).size = ${setOf(margarita, otraMargarita).size}")
        val catalogo: ProductoRepositorio = ProductoRepositorio(DatosIniciales.productos)
        val agregado: Boolean = catalogo.agregar(otraMargarita)
        appendLine("  Agregar #99 al catálogo → ${if (agregado) "agregado" else "rechazado (ya existe)"}")
    }

    private fun StringBuilder.reflection() {
        seccion("6. REFLECTION (propiedades y métodos)")
        for (clase in listOf(Cliente::class, Producto::class, Pedido::class)) {
            appendLine(Inspector.describir(clase))
        }
    }

    private val ANA: Cliente = Cliente(1, "Ana", "ana@duoc.cl", "-", "+56 (912) 345-6789", "Av. Siempre Viva 123")

    private fun producto(id: Int): Producto = DatosIniciales.productos.first { it.id == id }

    private fun detalle(pedido: Pedido): String =
        pedido.cantidades.entries.joinToString { "${it.value} x ${it.key.nombre}" }
}
