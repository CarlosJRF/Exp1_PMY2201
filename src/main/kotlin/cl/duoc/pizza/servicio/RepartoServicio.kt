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
        val repartidor: Repartidor = repartidores.sortedBy { it.distanciaKm }.find { it.disponible }
            ?: throw ErrorNegocio("No hay repartidores disponibles en este momento")

        repartidor.disponible = false
        pedido.repartidor = repartidor
        pedido.estado = EstadoPedido.EN_CAMINO
        return repartidor
    }
}
