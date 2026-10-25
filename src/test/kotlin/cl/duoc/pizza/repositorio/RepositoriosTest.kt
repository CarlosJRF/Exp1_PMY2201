package cl.duoc.pizza.repositorio

import cl.duoc.pizza.modelo.Categoria
import cl.duoc.pizza.modelo.Pedido
import cl.duoc.pizza.modelo.Usuario
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RepositoriosTest {
    private fun usuario(id: Int, email: String) =
        Usuario(id, "Ana", email, "secreto", "912345678", "Av. Siempre Viva 123")

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
    fun `cada producto del catalogo tiene stock inicial`() {
        val stock = StockRepositorio(DatosIniciales.stock)
        DatosIniciales.productos.forEach { assertTrue(stock.obtener(it.id) > 0) }
    }

    @Test
    fun `stock no acepta cantidades negativas`() {
        val stock = StockRepositorio(mapOf(1 to 5))
        stock.actualizar(1, 0)
        assertEquals(0, stock.obtener(1))
        assertEquals(0, stock.obtener(99))
        assertFailsWith<IllegalArgumentException> { stock.actualizar(1, -1) }
    }

    @Test
    fun `pedidos se listan por usuario`() {
        val ana = usuario(1, "ana@duoc.cl")
        val luis = usuario(2, "luis@duoc.cl")
        val repo = PedidoRepositorio()
        repo.guardar(Pedido(repo.siguienteId(), ana))
        repo.guardar(Pedido(repo.siguienteId(), luis))
        repo.guardar(Pedido(repo.siguienteId(), ana))

        assertEquals(listOf(1, 3), repo.listarPorUsuario(ana.id).map { it.id })
        assertNotNull(repo.buscarPorId(2))
    }
}
