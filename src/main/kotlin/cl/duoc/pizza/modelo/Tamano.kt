package cl.duoc.pizza.modelo

/** Tamaño de pizza; [porciones] multiplica la receta base (personal) para calcular sus ingredientes. */
enum class Tamano(val nombreVisible: String, val porciones: Int) {
    PERSONAL("Personal", 1),
    MEDIANA("Mediana", 2),
    FAMILIAR("Familiar", 3),
}
