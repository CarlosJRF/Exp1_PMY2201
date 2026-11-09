# DuocPizza 🍕

Aplicación de consola en **Kotlin** para una pizzería. Es un proyecto base sencillo,
pensado para evolucionar más adelante a una app móvil.

## Funcionalidades

- **Clientes:** registro e inicio de sesión. Las contraseñas se guardan con **Argon2id**
  (Bouncy Castle, sal aleatoria y formato PHC); nunca se guarda la contraseña original.
  El email se valida con una expresión regular (`nombre@dominio.com`) y el teléfono se formatea como
  `+XX (XXX) XXX-XXXX`; si alguno no es válido, la consola explica el error y lo vuelve a pedir.
  No se puede registrar un cliente igual a otro (mismo nombre y email).
- **Catálogo:** pizzas en 3 tamaños, combos, bebidas y acompañamientos; cada producto puede describirse
  con su precio e ingredientes. No admite productos repetidos (mismo nombre y categoría).
- **Promoción de temporada:** si la fecha del pedido está dentro del periodo de promoción, los productos
  marcados con la anotación `@Promocionable` (pizzas 10 %, combos 15 %) tienen descuento. En el catálogo
  se marcan con ★.
- **Inventario por ingredientes:** antes de aceptar un pedido se revisa cada ingrediente; si falta
  alguno se indica cuál es insuficiente y los pasos para reabastecerlo. Al confirmar, los ingredientes
  se verifican y descuentan en una sola operación atómica: o se descuentan todos, o ninguno.
- **Pedidos:** carrito (agregar de 1 a 100 unidades, quitar, notas opcionales), confirmación y cancelación.
  Estados: **En preparación → Listo → En camino**. Dos pedidos del mismo cliente se pueden combinar con `+`.
- **Totales:** 10 % de descuento si el pedido tiene **más de 3 pizzas**, descuento de la promoción de temporada
  e **IVA 19 %** sobre el monto descontado.
- **Repartidores:** se asigna el repartidor disponible más cercano; al registrar la entrega
  vuelve a quedar disponible.
- **Notificaciones** en consola al cliente (solo si tiene un email válido) cuando cambia su pedido.
- **Resumen del pedido:** datos del cliente, productos (nombre, precio, cantidad), subtotal,
  descuentos, IVA, total, estado y notas. Se muestra como confirmación al cliente.
- **Resumen de Kotlin avanzado** (menú principal, opción 3): muestra y guarda en `resumen-kotlin-avanzado.txt`
  las validaciones (Regex y Ranges), pedidos combinados con `+`, desestructuración de cliente y pedido,
  productos promocionables, igualdad de objetos (`equals`/`hashCode`) y la reflection de las clases principales.
- **Administrador:** inventario de ingredientes con aviso de stock bajo, reposición, listado de pedidos,
  reporte del día (completados y pendientes), cambio de estado, asignación de repartidor,
  registro de entrega y listado de usuarios y repartidores.

Los datos se guardan **en memoria**: al cerrar la aplicación se pierden.

## Requisitos

- JDK 21.
- No hace falta instalar Gradle ni Kotlin: el proyecto incluye Gradle Wrapper (`./gradlew`).
- Dependencias (se descargan solas): Bouncy Castle (Argon2id) y `kotlin-reflect` (anotaciones y reflection).

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

## Entrega en zip

```bash
git ls-files --cached --others --exclude-standard | zip -@ DuocPizza.zip
```

## Estructura

```
src/main/kotlin/cl/duoc/pizza/
├── Main.kt          # arma las dependencias e inicia la app
├── modelo/          # Usuario (Cliente, Repartidor, Administrador), Producto (Pizza, Combo, Bebida,
│                    # Acompanamiento), @Promocionable, Promociones, Pedido, Totales y enums
├── repositorio/     # almacenamiento en memoria y datos iniciales
├── servicio/        # reglas de negocio (usuarios, inventario, pedidos, reparto, validaciones,
│                    # resumen, reporte, reflection y resumen de Kotlin avanzado)
├── ui/              # menús y lectura por consola
└── util/            # hash de contraseñas Argon2id y formato de precios/fechas
```

La lógica de negocio (`modelo`, `repositorio`, `servicio`) no depende de la consola,
por lo que puede reutilizarse en una futura app Android cambiando solo la capa `ui`.
