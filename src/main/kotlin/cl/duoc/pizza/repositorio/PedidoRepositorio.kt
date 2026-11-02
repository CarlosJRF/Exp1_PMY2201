package cl.duoc.pizza.repositorio

import cl.duoc.pizza.modelo.Pedido

class PedidoRepositorio {
    private val pedidos: MutableMap<Int, Pedido> = mutableMapOf<Int, Pedido>()
    private var ultimoId: Int = 0

    fun siguienteId(): Int = ++ultimoId

    fun guardar(pedido: Pedido) {
        pedidos[pedido.id] = pedido
        ultimoId = maxOf(ultimoId, pedido.id)
    }

    fun buscarPorId(id: Int): Pedido? = pedidos[id]

    fun eliminar(id: Int) {
        pedidos.remove(id)
    }

    fun listarPorCliente(idCliente: Int): List<Pedido> =
        listar().filter { it.cliente.id == idCliente }

    fun listar(): List<Pedido> = pedidos.values.sortedBy { it.id }
}
