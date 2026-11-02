# DuocPizza 🍕

Aplicación de consola en **Kotlin** para una pizzería. Es un proyecto base sencillo,
pensado para evolucionar más adelante a una app móvil.

## Funcionalidades

- **Clientes:** registro e inicio de sesión. Las contraseñas se guardan con **Argon2id**
  (Bouncy Castle, sal aleatoria y formato PHC); nunca se guarda la contraseña original. Si el email o el teléfono
  no tienen un formato válido se registran con valores predeterminados (`correo@invalido.com`,
  `(000) 000-0000`) y se avisa al usuario.
- **Catálogo:** pizzas en 3 tamaños, bebidas y acompañamientos; cada producto puede describirse
  con su precio e ingredientes.
- **Inventario por ingredientes:** antes de aceptar un pedido se revisa cada ingrediente; si falta
  alguno se indica cuál es insuficiente y los pasos para reabastecerlo. Al confirmar, cada producto
  se prepara como un lote que descuenta sus ingredientes.
- **Pedidos:** carrito (agregar, quitar, notas opcionales), confirmación y cancelación.
  Estados: **En preparación → Listo → En camino**.
- **Totales:** 10 % de descuento si el pedido tiene **más de 3 pizzas** e **IVA 19 %** sobre el monto descontado.
- **Repartidores:** se asigna el repartidor disponible más cercano; al registrar la entrega
  vuelve a quedar disponible.
- **Notificaciones** en consola al cliente (solo si tiene un email válido) cuando cambia su pedido.
- **Resumen del pedido:** datos del cliente, productos (nombre, precio, cantidad), subtotal,
  descuento, IVA, total, estado y notas. Se muestra como confirmación al cliente.
- **Administrador:** inventario de ingredientes con aviso de stock bajo, reposición, listado de pedidos,
  reporte del día (completados y pendientes), cambio de estado, asignación de repartidor,
  registro de entrega y listado de usuarios y repartidores.

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

## Entrega en zip

```bash
git ls-files --cached --others --exclude-standard | zip -@ DuocPizza.zip
```

## Estructura

```
src/main/kotlin/cl/duoc/pizza/
├── Main.kt          # arma las dependencias e inicia la app
├── modelo/          # Usuario (Cliente, Repartidor, Administrador), Producto, Pedido, Totales y enums
├── repositorio/     # almacenamiento en memoria y datos iniciales
├── servicio/        # reglas de negocio (usuarios, inventario, pedidos, reparto, resumen, reporte)
├── ui/              # menús y lectura por consola
└── util/            # hash de contraseñas Argon2id y formato de precios/fechas
```

La lógica de negocio (`modelo`, `repositorio`, `servicio`) no depende de la consola,
por lo que puede reutilizarse en una futura app Android cambiando solo la capa `ui`.
