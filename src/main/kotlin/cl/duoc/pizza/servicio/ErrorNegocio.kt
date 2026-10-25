package cl.duoc.pizza.servicio

/** Error de una regla de negocio; su mensaje se muestra tal cual al usuario. */
class ErrorNegocio(mensaje: String) : Exception(mensaje)
