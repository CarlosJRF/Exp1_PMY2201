package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.EstadoPedido
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Repartidor

class RepartoServicio(private val repartidores: List<Repartidor>) {

    fun repartidores(): List<Repartidor> = repartidores

    /** Asigna el pedido listo al repartidor disponible más cercano y lo deja en camino. */
    fun asignar(pedido: Pedido): Repartidor {
        if (pedido.estado != EstadoPedido.LISTO) {
            throw ErrorNegocio("Solo se pueden asignar pedidos listos (el #${pedido.id} está ${pedido.estadoVisible.lowercase()})")
        }
        val repartidor: Repartidor = repartidores.filter { it.disponible }.minByOrNull { it.distanciaKm }
            ?: throw ErrorNegocio("No hay repartidores disponibles en este momento")

        repartidor.tomarPedido(pedido)
        return repartidor
    }

    /** Registra la entrega del pedido: su repartidor queda disponible para otro reparto. */
    fun entregar(pedido: Pedido): Repartidor {
        val repartidor: Repartidor = pedido.repartidor
            ?: throw ErrorNegocio("El pedido #${pedido.id} no tiene repartidor asignado")
        if (pedido.entregado) throw ErrorNegocio("El pedido #${pedido.id} ya fue entregado")
        repartidor.entregarPedido(pedido)
        return repartidor
    }
}
