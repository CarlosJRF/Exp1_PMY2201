package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Administrador
import cl.duoc.pizza.modelo.Cliente
import cl.duoc.pizza.modelo.ContactoPredeterminado
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.servicio.PedidoServicio
import cl.duoc.pizza.servicio.RepartoServicio
import cl.duoc.pizza.servicio.ResumenKotlinAvanzado
import cl.duoc.pizza.servicio.UsuarioServicio
import cl.duoc.pizza.servicio.Validaciones
import java.io.File
import java.io.IOException

private const val ARCHIVO_RESUMEN = "resumen-kotlin-avanzado.txt"

class MenuPrincipal(
    private val consola: Consola,
    private val usuarioServicio: UsuarioServicio,
    private val inventario: InventarioServicio,
    private val pedidoServicio: PedidoServicio,
    private val reparto: RepartoServicio,
) {

    fun iniciar() {
        consola.mensaje("🍕 Bienvenido a DuocPizza 🍕")
        while (true) {
            val opciones: List<String> = listOf("Registrarse", "Iniciar sesión", "Ver resumen de sistema en consola")
            when (consola.menu("Menú principal", opciones, "Salir")) {
                1 -> consola.intentar { registrar() }
                2 -> consola.intentar { iniciarSesion() }
                3 -> verResumenKotlinAvanzado()
                0 -> return
            }
        }
    }

    private fun registrar() {
        consola.titulo("Registro de usuario")
        val nombre: String = consola.leerTexto("Nombre")
        val email: String = consola.leerValido("Email", Validaciones.MENSAJE_EMAIL) { texto ->
            texto.takeIf { Validaciones.esEmailValido(it) }
        }
        val password: String = consola.leerPassword("Contraseña (mín. ${UsuarioServicio.LARGO_MINIMO_PASSWORD} caracteres)")
        val telefono: String = consola.leerValido("Teléfono", Validaciones.MENSAJE_TELEFONO) { Validaciones.formatearTelefono(it) }
        consola.mensaje("Teléfono registrado como $telefono")
        val cliente: Cliente = usuarioServicio.registrar(nombre, email, password, telefono, consola.leerTexto("Dirección"))
        consola.exito("Usuario registrado. ¡Hola, ${cliente.nombre}!")
        if (cliente.email == ContactoPredeterminado.EMAIL) {
            consola.error("El email no era válido: se registró como ${cliente.email} (úsalo para iniciar sesión)")
        }
        if (cliente.telefono == ContactoPredeterminado.TELEFONO) {
            consola.error("El teléfono no era válido: se registró como ${cliente.telefono}")
        }
        abrirSesion(cliente)
    }

    /** Muestra el resumen de los conceptos avanzados y lo guarda en [ARCHIVO_RESUMEN]. */
    private fun verResumenKotlinAvanzado() {
        val resumen: String = ResumenKotlinAvanzado.generar()
        consola.mensaje(resumen)
        try {
            File(ARCHIVO_RESUMEN).writeText(resumen)
            consola.exito("Resumen guardado en $ARCHIVO_RESUMEN")
        } catch (e: IOException) {
            consola.error("No se pudo guardar el resumen: ${e.message}")
        }
    }

    private fun iniciarSesion() {
        consola.titulo("Iniciar sesión")
        val usuario: Usuario = usuarioServicio.iniciarSesion(
            email = consola.leerTexto("Email"),
            password = consola.leerPassword("Contraseña"),
        )
        consola.exito("Sesión iniciada. ¡Hola, ${usuario.nombre}!")
        abrirSesion(usuario)
    }

    private fun abrirSesion(usuario: Usuario) {
        when (usuario) {
            is Administrador -> MenuAdmin(consola, inventario, pedidoServicio, reparto, usuarioServicio).mostrar()
            is Cliente -> MenuCliente(usuario, consola, inventario, pedidoServicio).mostrar()
            else -> consola.error("Este usuario no tiene un menú asignado")
        }
        consola.exito("Sesión cerrada")
    }
}
