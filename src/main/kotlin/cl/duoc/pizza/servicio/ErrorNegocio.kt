package cl.duoc.pizza.servicio

/** Error de una regla de negocio; su mensaje se muestra tal cual al usuario. */
open class ErrorNegocio(mensaje: String) : Exception(mensaje)

/** No alcanzan los ingredientes; [faltantes] detalla cuáles y cuánto falta. */
class StockInsuficiente(val faltantes: List<IngredienteFaltante>, mensaje: String) : ErrorNegocio(mensaje)
