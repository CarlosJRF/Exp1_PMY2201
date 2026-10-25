# DuocPizza 🍕

Aplicación de consola en **Kotlin** para una pizzería. Es un proyecto base sencillo,
pensado para evolucionar más adelante a una app móvil.

## Funcionalidades

- Registro de usuarios e inicio de sesión (contraseñas guardadas con hash SHA-256).
- Catálogo de pizzas (3 tamaños), bebidas y acompañamientos.
- Pedidos: agregar y quitar productos, confirmar y cancelar.
- Control de stock: se valida al agregar, se descuenta al confirmar y se devuelve al cancelar.
- Resumen de pedido con detalle, total y estado.
- Menú de administrador: inventario con aviso de stock bajo, reposición y listado de pedidos.

Los datos se guardan **en memoria**: al cerrar la aplicación se pierden.

## Requisitos

- JDK 21.
- No hace falta instalar Gradle ni Kotlin: el proyecto incluye Gradle Wrapper (`./gradlew`).

## Cómo ejecutar

```bash
./gradlew run -q --console=plain
```

En Windows usar `gradlew.bat` en lugar de `./gradlew`.

Usuario administrador precargado:

| Email | Contraseña |
|---|---|
| `admin@duocpizza.cl` | `admin123` |

Los clientes se crean desde la opción **Registrarse** del menú principal.

## Tests

```bash
./gradlew test
```

## Estructura

```
src/main/kotlin/cl/duoc/pizza/
├── Main.kt          # arma las dependencias e inicia la app
├── modelo/          # Usuario, Producto, Pedido, ItemPedido y enums
├── repositorio/     # almacenamiento en memoria y datos iniciales
├── servicio/        # reglas de negocio (usuarios, inventario, pedidos, resumen)
├── ui/              # menús y lectura por consola
└── util/            # hash SHA-256 y formato de precios/fechas
```

La lógica de negocio (`modelo`, `repositorio`, `servicio`) no depende de la consola,
por lo que puede reutilizarse en una futura app Android cambiando solo la capa `ui`.
