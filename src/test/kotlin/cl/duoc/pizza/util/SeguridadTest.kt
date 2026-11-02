package cl.duoc.pizza.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SeguridadTest {

    @Test
    fun `el hash usa formato PHC de argon2id con los parametros configurados`() {
        val hash: String = Seguridad.hashPassword("secreto123")
        assertTrue(hash.startsWith("\$argon2id\$v=19\$m=19456,t=2,p=1\$"), hash)
        assertFalse(hash.contains("secreto123"))
    }

    @Test
    fun `la misma contrasena genera hashes distintos por la sal aleatoria`() {
        assertNotEquals(Seguridad.hashPassword("secreto123"), Seguridad.hashPassword("secreto123"))
    }

    @Test
    fun `verifica la contrasena correcta y rechaza la incorrecta`() {
        val hash: String = Seguridad.hashPassword("secreto123")
        assertTrue(Seguridad.verificarPassword("secreto123", hash))
        assertFalse(Seguridad.verificarPassword("secreto124", hash))
        assertFalse(Seguridad.verificarPassword("", hash))
    }

    @Test
    fun `coincide con hashes generados por libsodium`() {
        // Vectores generados con libsodium (PyNaCl), una implementación de Argon2id independiente de Bouncy Castle
        val salFija: String = "\$argon2id\$v=19\$m=19456,t=2,p=1\$RHVvY1BpenphLXNhbC0xNg\$2l0A9rE4+5uHXNvncK9ZMmb7oJ0Q1AAHKJ5YlaI+n34"
        val salAleatoria: String = "\$argon2id\$v=19\$m=19456,t=2,p=1\$roKwgsw9Wa4pcl65JgsmLQ\$GXINpLpCOa5OzuzhC0Pi5dqn99GT2wE5noSPni6hIUo"
        assertTrue(Seguridad.verificarPassword("secreto123", salFija))
        assertTrue(Seguridad.verificarPassword("secreto123", salAleatoria))
        assertFalse(Seguridad.verificarPassword("Secreto123", salAleatoria))
    }

    @Test
    fun `un hash con formato invalido no se acepta`() {
        assertFalse(Seguridad.verificarPassword("secreto123", "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"))
        assertFalse(Seguridad.verificarPassword("secreto123", "\$argon2i\$v=19\$m=19456,t=2,p=1\$c2Fs\$aGFzaA"))
        assertFalse(Seguridad.verificarPassword("secreto123", "\$argon2id\$v=19\$m=x,t=2,p=1\$c2Fs\$aGFzaA"))
        assertFalse(Seguridad.verificarPassword("secreto123", "\$argon2id\$v=19\$t=2,p=1\$c2Fs\$aGFzaA"))
        assertFalse(Seguridad.verificarPassword("secreto123", "\$argon2id\$v=19\$m,t=2,p=1\$c2Fs\$aGFzaA"))
        assertFalse(Seguridad.verificarPassword("secreto123", "\$argon2id\$v=19\$m=19456,t=2,p=1\$no-es-base64!\$aGFzaA"))
    }
}
