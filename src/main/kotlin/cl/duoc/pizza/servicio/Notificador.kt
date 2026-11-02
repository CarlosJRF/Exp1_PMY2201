package cl.duoc.pizza.servicio

import cl.duoc.pizza.modelo.Pedido

/** Arma las notificaciones para el cliente de un pedido. Por ahora se muestran en consola. */
object Notificador {

    /** Texto de la notificación sobre el estado del pedido, o null si el cliente no tiene un email válido. */
    fun notificar(pedido: Pedido): String? =
        pedido.cliente.emailNotificaciones?.let { email ->
            val novedad: String = if (pedido.entregado) "fue entregado" else "está ${pedido.estadoVisible.lowercase()}"
            "✉ Notificación a $email: tu pedido #${pedido.id} $novedad"
        }
}
