package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.servicio.PedidoServicio
import cl.duoc.pizza.servicio.UsuarioServicio

class MenuPrincipal(
    private val consola: Consola,
    private val usuarioServicio: UsuarioServicio,
    private val inventario: InventarioServicio,
    private val pedidoServicio: PedidoServicio,
) {

    fun iniciar() {
        consola.mensaje("🍕 Bienvenido a DuocPizza 🍕")
        while (true) {
            when (consola.menu("Menú principal", listOf("Registrarse", "Iniciar sesión"), "Salir")) {
                1 -> consola.intentar { registrar() }
                2 -> consola.intentar { iniciarSesion() }
                0 -> return
            }
        }
    }

    private fun registrar() {
        consola.titulo("Registro de usuario")
        val usuario: Usuario = usuarioServicio.registrar(
            nombre = consola.leerTexto("Nombre"),
            email = consola.leerTexto("Email"),
            password = consola.leerPassword("Contraseña (mín. ${UsuarioServicio.LARGO_MINIMO_PASSWORD} caracteres)"),
            telefono = consola.leerTexto("Teléfono"),
            direccion = consola.leerTexto("Dirección"),
        )
        consola.exito("Usuario registrado. ¡Hola, ${usuario.nombre}!")
        abrirSesion(usuario)
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
        if (usuario.esAdmin) {
            MenuAdmin(consola, inventario, pedidoServicio).mostrar()
        } else {
            MenuCliente(usuario, consola, inventario, pedidoServicio).mostrar()
        }
        consola.exito("Sesión cerrada")
    }
}
