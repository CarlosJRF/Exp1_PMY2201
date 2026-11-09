package cl.duoc.pizza.repositorio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Tamano
import cl.duoc.pizza.modelo.Cliente
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RepositoriosTest {
    private fun usuario(id: Int, email: String) =
        Cliente(id, "Ana", email, "secreto", "912345678", "Av. Siempre Viva 123")

    @Test
    fun `usuario se busca por email sin distinguir mayusculas`() {
        val repo = UsuarioRepositorio()
        repo.guardar(usuario(repo.siguienteId(), "ana@duoc.cl"))

        assertNotNull(repo.buscarPorEmail(" ANA@duoc.cl "))
        assertNull(repo.buscarPorEmail("otro@duoc.cl"))
    }

    @Test
    fun `los ids continuan despues de los usuarios iniciales`() {
        val repo = UsuarioRepositorio(listOf(DatosIniciales.admin))
        assertEquals(DatosIniciales.admin.id + 1, repo.siguienteId())
    }

    @Test
    fun `catalogo inicial tiene ids unicos y todas las categorias`() {
        val ids = DatosIniciales.productos.map { it.id }
        assertEquals(ids.size, ids.toSet().size)

        val repo = ProductoRepositorio(DatosIniciales.productos)
        Categoria.entries.forEach { assertTrue(repo.listarPorCategoria(it).isNotEmpty()) }
    }

    @Test
    fun `cada ingrediente del catalogo tiene stock inicial`() {
        val stock = StockRepositorio(DatosIniciales.ingredientes)
        DatosIniciales.productos.flatMap { it.ingredientes.keys }.forEach { assertTrue(stock.obtener(it) > 0, it) }
    }

    @Test
    fun `los ingredientes de la pizza escalan con el tamano`() {
        val familiar = DatosIniciales.productos.first { it.nombre == "Margarita Familiar" && it.tamano == Tamano.FAMILIAR }
        assertEquals(3, familiar.ingredientes["Masa"])
    }

    @Test
    fun `aplicar cambios es todo o nada`() {
        val stock = StockRepositorio(mapOf("Masa" to 5, "Queso" to 1))
        assertFailsWith<IllegalArgumentException> { stock.aplicar(mapOf("Masa" to -2, "Queso" to -3)) }
        assertEquals(mapOf("Masa" to 5, "Queso" to 1), stock.listar())

        stock.aplicar(mapOf("Masa" to -2, "Queso" to -1))
        assertEquals(mapOf("Masa" to 3, "Queso" to 0), stock.listar())
    }

    @Test
    fun `stock no acepta cantidades negativas`() {
        val stock = StockRepositorio(mapOf("Masa" to 5))
        stock.actualizar("Masa", 0)
        assertEquals(0, stock.obtener("Masa"))
        assertEquals(0, stock.obtener("Harina"))
        assertFailsWith<IllegalArgumentException> { stock.actualizar("Masa", -1) }
    }

    @Test
    fun `pedidos se listan por usuario`() {
        val ana = usuario(1, "ana@duoc.cl")
        val luis = usuario(2, "luis@duoc.cl")
        val repo = PedidoRepositorio()
        repo.guardar(Pedido(repo.siguienteId(), ana))
        repo.guardar(Pedido(repo.siguienteId(), luis))
        repo.guardar(Pedido(repo.siguienteId(), ana))

        assertEquals(listOf(1, 3), repo.listarPorCliente(ana.id).map { it.id })
        assertNotNull(repo.buscarPorId(2))
    }
}
