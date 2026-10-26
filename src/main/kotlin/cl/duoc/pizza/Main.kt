package cl.duoc.pizza

import cl.duoc.pizza.repositorio.DatosIniciales
import cl.duoc.pizza.repositorio.PedidoRepositorio
import cl.duoc.pizza.repositorio.ProductoRepositorio
import cl.duoc.pizza.repositorio.StockRepositorio
import cl.duoc.pizza.repositorio.UsuarioRepositorio
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.servicio.PedidoServicio
import cl.duoc.pizza.servicio.UsuarioServicio
import cl.duoc.pizza.ui.Consola
import cl.duoc.pizza.ui.EntradaFinalizada
import cl.duoc.pizza.ui.MenuPrincipal

fun main() {
    // Repositorios (datos en memoria)
    val usuarioRepositorio: UsuarioRepositorio = UsuarioRepositorio(listOf(DatosIniciales.admin))
    val productoRepositorio: ProductoRepositorio = ProductoRepositorio(DatosIniciales.productos)
    val stockRepositorio: StockRepositorio = StockRepositorio(DatosIniciales.stock)
    val pedidoRepositorio: PedidoRepositorio = PedidoRepositorio()

    // Servicios (reglas de negocio)
    val usuarioServicio: UsuarioServicio = UsuarioServicio(usuarioRepositorio)
    val inventarioServicio: InventarioServicio = InventarioServicio(productoRepositorio, stockRepositorio)
    val pedidoServicio: PedidoServicio = PedidoServicio(pedidoRepositorio, inventarioServicio)

    // Interfaz de consola
    val consola: Consola = Consola()
    try {
        MenuPrincipal(consola, usuarioServicio, inventarioServicio, pedidoServicio).iniciar()
    } catch (e: EntradaFinalizada) {
        consola.mensaje("")
    }
    consola.mensaje("¡Hasta pronto! 🍕")
}
