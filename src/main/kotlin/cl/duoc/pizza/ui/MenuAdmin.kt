package cl.duoc.pizza.ui

import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Repartidor
import cl.duoc.pizza.modelo.Usuario
import cl.duoc.pizza.servicio.ErrorNegocio
import cl.duoc.pizza.servicio.InventarioServicio
import cl.duoc.pizza.servicio.Notificador
import cl.duoc.pizza.servicio.PedidoServicio
import cl.duoc.pizza.servicio.RepartoServicio
import cl.duoc.pizza.servicio.ReporteDiario
import cl.duoc.pizza.servicio.UsuarioServicio

class MenuAdmin(
    private val consola: Consola,
    private val inventario: InventarioServicio,
    private val pedidoServicio: PedidoServicio,
    private val reparto: RepartoServicio,
    private val usuarioServicio: UsuarioServicio,
) {

    companion object {
        private const val REPOSICION_MAXIMA = 1000
    }

    private val opciones: List<String> = listOf(
        "Ver inventario de ingredientes",
        "Reponer ingrediente",
        "Ver todos los pedidos",
        "Reporte del día",
        "Marcar pedido como listo",
        "Asignar repartidor a un pedido listo",
        "Registrar entrega de un pedido",
        "Ver usuarios y repartidores",
    )

    fun mostrar() {
        while (true) {
            val opcion: Int = consola.menu("Menú administrador", opciones, "Cerrar sesión")
            consola.intentar {
                when (opcion) {
                    1 -> verInventario()
                    2 -> reponerIngrediente()
                    3 -> verPedidos()
                    4 -> verReporteDelDia()
                    5 -> marcarListo()
                    6 -> asignarRepartidor()
                    7 -> registrarEntrega()
                    8 -> verUsuarios()
                }
            }
            if (opcion == 0) return
        }
    }

    private fun verInventario() {
        consola.titulo("Inventario de ingredientes")
        consola.mensaje(Vistas.ingredientes(inventario))
        val stockBajo: List<String> = inventario.ingredientesConStockBajo()
        if (stockBajo.isNotEmpty()) {
            consola.mensaje("")
            consola.error("Stock bajo: ${stockBajo.joinToString()}")
        }
    }

    private fun reponerIngrediente() {
        verInventario()
        val nombres: List<String> = inventario.ingredientes().keys.toList()
        val numero: Int = consola.leerEntero("Número del ingrediente (0 para volver)", 0, nombres.size)
        if (numero == 0) return
        val ingrediente: String = nombres[numero - 1]
        while (true) {
            val cantidad: Int = consola.leerEntero("Cantidad a agregar", -REPOSICION_MAXIMA, REPOSICION_MAXIMA)
            try {
                inventario.reponer(ingrediente, cantidad)
                consola.exito("Stock de $ingrediente: ${inventario.stockDe(ingrediente)}")
                return
            } catch (e: ErrorNegocio) {
                consola.error("${e.message}. Ingresa una cantidad entre 1 y $REPOSICION_MAXIMA.")
            }
        }
    }

    private fun verPedidos() {
        consola.titulo("Todos los pedidos")
        val pedidos: List<Pedido> = pedidoServicio.todosLosPedidos()
        if (pedidos.isEmpty()) {
            consola.mensaje("No hay pedidos")
            return
        }
        pedidos.forEach { consola.mensaje(Vistas.lineaPedido(it)) }
    }

    private fun verReporteDelDia() {
        consola.titulo("Reporte del día")
        consola.mensaje(ReporteDiario.generar(pedidoServicio.pedidosDelDia()))
    }

    private fun marcarListo() {
        val pedido: Pedido = elegirPedido() ?: return
        pedidoServicio.marcarListo(pedido)
        consola.exito("Pedido #${pedido.id}: ${pedido.estadoVisible}")
        notificar(pedido)
    }

    private fun asignarRepartidor() {
        consola.titulo("Repartidores")
        reparto.repartidores().forEach { consola.mensaje(it.descripcion()) }
        val pedido: Pedido = elegirPedido() ?: return
        val repartidor: Repartidor = reparto.asignar(pedido)
        consola.exito("Pedido #${pedido.id} asignado a ${repartidor.nombre} (${repartidor.distanciaKm} km): ${pedido.estadoVisible}")
        notificar(pedido)
    }

    private fun registrarEntrega() {
        val pedido: Pedido = elegirPedido() ?: return
        val repartidor: Repartidor = reparto.entregar(pedido)
        consola.exito("Pedido #${pedido.id} entregado por ${repartidor.nombre}, que queda disponible")
        notificar(pedido)
    }

    /** Lista clientes, administradores y repartidores; cada uno se describe a su manera (polimorfismo). */
    private fun verUsuarios() {
        consola.titulo("Usuarios y repartidores")
        val personas: List<Usuario> = usuarioServicio.listar() + reparto.repartidores()
        for (persona in personas) {
            consola.mensaje(persona.descripcion())
        }
    }

    /** Muestra la notificación solo si el cliente tiene un email válido. */
    private fun notificar(pedido: Pedido) {
        Notificador.notificar(pedido)?.let { consola.mensaje(it) }
    }

    /** Muestra los pedidos y devuelve el elegido, o null si el usuario vuelve. */
    private fun elegirPedido(): Pedido? {
        verPedidos()
        val id: Int = consola.leerEntero("Número de pedido (0 para volver)", 0, Int.MAX_VALUE)
        return if (id == 0) null else pedidoServicio.buscarPedido(id)
    }
}
