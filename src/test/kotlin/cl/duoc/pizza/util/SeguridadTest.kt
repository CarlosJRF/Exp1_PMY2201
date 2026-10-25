package cl.duoc.pizza.util

import kotlin.test.Test
import kotlin.test.assertEquals

class SeguridadTest {
    @Test
    fun `hash sha256 conocido`() {
        assertEquals(
            "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
            Seguridad.hashSha256("hello"),
        )
    }
}
