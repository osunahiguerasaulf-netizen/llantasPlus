# LlantasPlus — catálogo y solicitudes por WhatsApp

Proyecto Android con Kotlin y XML tradicional, Activities por pantalla, ViewBinding (`b`), métodos `iniciarComponentes()` y `eventosClick()`. Conserva los colores, logo, banners editables y categorías con imágenes del proyecto original.

## Abrir

1. Extrae TODO el ZIP en una carpeta normal; no lo abras dentro del compresor.
2. Android Studio → Open → selecciona la carpeta `LlantasPlus` que contiene `settings.gradle.kts`.
3. Permite la sincronización y la instalación del SDK solicitado. Se conserva AGP 9.3.3 y Gradle 9.5.0 del proyecto recibido. Necesitas una versión compatible de Android Studio, SDK 37.0 y JDK 17 o superior compatible con Gradle. Puedes usar el JDK integrado de Android Studio; se eliminó la obligación anterior de descargar JDK 25.
4. Ejecuta `app` en un teléfono Android 7.0 o posterior (API 24+) o en un emulador. La primera sincronización requiere Internet.
5. Pulsa Registrarse y crea tu cuenta; luego inicia sesión con usuario o correo y contraseña. No se incluyen usuarios ni contraseñas predeterminados.

## Configurar el WhatsApp real del negocio

No se proporcionó el teléfono del negocio: NO se incluyó un número inventado.

- Para probar: agrega productos → Carrito → Preparar pedido → Configurar WhatsApp de la tienda. Escribe el número internacional real del destinatario. En México: `52` seguido de los diez dígitos, sin el antiguo `1` adicional.
- Para dejarlo predeterminado al distribuir: edita `app/src/main/res/values/negocio.xml`, recurso `lp_whatsapp_negocio`. Usa únicamente dígitos, sin `+`, guiones ni espacios. Compila de nuevo. Una configuración guardada en el teléfono tiene prioridad sobre ese valor.
- Revisa siempre el destinatario en la confirmación. El campo corresponde al NEGOCIO, no al número del cliente.
- Abrir WhatsApp prepara el mensaje; el cliente debe pulsar Enviar allí. Se intenta WhatsApp, WhatsApp Business y finalmente el enlace web. Si no hay aplicación que lo abra, se ofrece copiar el resumen.
- La app NO realiza cobros, NO verifica recepción, NO reserva inventario y NO marca ventas como pagadas. El historial solo registra solicitudes preparadas. El carrito se conserva para evitar perderlo al cancelar WhatsApp; se puede vaciar manualmente.

## Funciones terminadas

- Registro y acceso locales; validación de campos, contraseña oculta/visible y cierre de sesión.
- Inicio: banners XML con acciones, categorías ilustradas, destacados y búsqueda mediante la tecla Buscar del teclado.
- Catálogo: búsqueda por marca/modelo/medida, categorías, filtros combinables de marca, medida, precio, ofertas y favoritos, orden ascendente/descendente y limpiar filtros.
- Detalle: imagen, medida, precio, descuento y advertencia de compatibilidad.
- Favoritos y carrito guardados por cuenta incluso después de cerrar la app.
- Carrito: aumentar/disminuir entre 1 y 24 unidades por producto, eliminar, vaciar y calcular total con descuentos en centavos.
- Pedido: nombre, contacto, recoger o solicitar envío, domicilio condicionado a envío, notas, folio, resumen y revisión antes de abrir WhatsApp.
- Perfil: datos editables, favoritos, historial local de hasta 50 solicitudes y cerrar sesión.
- Los márgenes de barras del sistema y teclado se gestionan en `PantallaActivity` sin duplicar `fitsSystemWindows`.

## Catálogo y precios

Los seis productos y fotografías recibidos son ILUSTRATIVOS. Los precios y descuentos son ejemplos; no se verificaron existencias ni compatibilidades comerciales.

Edita `app/src/main/assets/catalogo.json` para usar el catálogo real. Cada producto contiene:

- `id`: entero único, estable. No recicles IDs al sustituir un producto.
- `marca`, `modelo`, `medida` y `categoria` (Automóvil, SUV o Camioneta).
- `precioCentavos`: precio ANTES del descuento. Ejemplo: 245000 representa $2,450.00.
- `descuento`: entero entre 0 y 100. El precio final se calcula una vez; todos los totales usan ese resultado.
- `imagen`: nombre del recurso sin extensión, por ejemplo `lp_llanta_1`.

Si incorporas productos o cambias las categorías, ajusta los filtros y verifica el catálogo. Antes de operar con datos reales, reemplaza los textos de demostración en las pantallas y en `PedidoResumen.kt`, ajusta la política de precios/envío/impuestos a la tienda y prueba el destinatario real. No hay editor de inventario dentro de la app.

## Datos y alcance

Las cuentas se guardan en SQLite en este dispositivo, con contraseñas derivadas usando PBKDF2 y sal aleatoria; no se guardan contraseñas en texto plano. El cálculo de credenciales se ejecuta fuera del hilo de interfaz. No hay backend ni autenticación remota. No se valida la titularidad del correo y no existe recuperación por correo; el enlace del login explica esta limitación.

Carrito, favoritos, perfil e historial se guardan localmente por cuenta. Cambiar de equipo o borrar los datos de la app los pierde. No se incluyen credenciales, datos personales de clientes, configuración del SDK del equipo del desarrollador ni claves privadas en el ZIP. Para una tienda con cuentas compartidas entre dispositivos, inventario actualizado o administración remota, se requiere un backend adicional.

## Estructura

- `MainActivity` / `RegistroActivity`: acceso y cuentas.
- `PrincipalActivity` / `CatalogoActivity` / `DetalleActivity`: exploración.
- `CarritoActivity` / `PedidoActivity` / `PerfilActivity`: solicitudes y datos del cliente.
- `Producto`, `DatosTienda`, `Tarjetas`: catálogo y tarjetas reutilizables.
- `Sesion`: persistencia por cuenta, cantidades, favoritos e historial.
- `UsuariosBD`: cuentas SQLite.
- `PedidoResumen`: cálculos y texto del pedido.
- `PantallaActivity`: márgenes y navegación comunes.

## Prueba manual en tu teléfono

1. Registra una cuenta; prueba acceso por usuario y por correo, contraseña incorrecta y campos vacíos.
2. Busca una medida; combina categoría, marca, precio y ofertas. Prueba una búsqueda sin resultados y Limpiar filtros.
3. Marca favoritos, abre un detalle, agrega dos modelos y modifica sus cantidades en el carrito. Verifica los precios y descuentos.
4. Cierra y abre la app; comprueba que el carrito permanece. Cierra sesión, crea otra cuenta y verifica que sus datos estén separados.
5. Configura el WhatsApp real de la tienda; solicita envío y prueba el campo de domicilio obligatorio. Revisa el total, destino y resumen.
6. Abre WhatsApp, verifica destinatario y texto SIN necesidad de enviar una solicitud de prueba. Regresa y comprueba que el carrito no desaparece y el historial dice envío sin verificar.
7. Gira el teléfono, abre el teclado y prueba el tamaño de texto del sistema. Verifica también WhatsApp Business o la alternativa de copiar.

El resultado de las comprobaciones automatizadas realizadas durante la entrega aparece en `VALIDACION.md`.

## Referencias

- WhatsApp Click to Chat: https://faq.whatsapp.com/5913398998672934
- Intents de Android: https://developer.android.com/training/basics/intents/sending
