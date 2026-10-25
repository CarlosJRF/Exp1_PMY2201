package cl.duoc.pizza.repositorio

import cl.duoc.pizza.modelo.Pedido

class PedidoRepositorio {
    private val pedidos = mutableMapOf<Int, Pedido>()
    private var ultimoId = 0

    fun siguienteId(): Int = ++ultimoId

    fun guardar(pedido: Pedido) {
        pedidos[pedido.id] = pedido
        ultimoId = maxOf(ultimoId, pedido.id)
    }

    fun buscarPorId(id: Int): Pedido? = pedidos[id]

    fun listarPorUsuario(idUsuario: Int): List<Pedido> =
        listar().filter { it.usuario.id == idUsuario }

    fun listar(): List<Pedido> = pedidos.values.sortedBy { it.id }
}
