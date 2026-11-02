package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Administrador
import cl.duoc.pizza.modelo.Cliente
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.servicio.PedidoServicio
import cl.duoc.pizza.servicio.RepartoServicio
import cl.duoc.pizza.servicio.UsuarioServicio

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
            when (consola.menu("Menú principal", listOf("Registrarse", "Iniciar sesión"), "Salir")) {
                1 -> consola.intentar { registrar() }
                2 -> consola.intentar { iniciarSesion() }
                0 -> return
            }
        }
    }

    private fun registrar() {
        consola.titulo("Registro de usuario")
        val cliente: Cliente = usuarioServicio.registrar(
            nombre = consola.leerTexto("Nombre"),
            email = consola.leerTexto("Email"),
            password = consola.leerPassword("Contraseña (mín. ${UsuarioServicio.LARGO_MINIMO_PASSWORD} caracteres)"),
            telefono = consola.leerTexto("Teléfono"),
            direccion = consola.leerTexto("Dirección"),
        )
        consola.exito("Usuario registrado. ¡Hola, ${cliente.nombre}!")
        if (cliente.email == UsuarioServicio.EMAIL_PREDETERMINADO) {
            consola.error("El email no era válido: se registró como ${cliente.email} (úsalo para iniciar sesión)")
        }
        if (cliente.telefono == UsuarioServicio.TELEFONO_PREDETERMINADO) {
            consola.error("El teléfono no era válido: se registró como ${cliente.telefono}")
        }
        abrirSesion(cliente)
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
