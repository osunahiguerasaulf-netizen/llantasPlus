# Validación de entrega — 30 de octubre de 2026

- `assembleDebug`: correcto. Se generó el APK de depuración desde el proyecto final.
- `testDebugUnitTest`: 5 tests ejecutados; 0 fallos. Incluye cuatro pruebas específicas de descuentos, totales de una y varias líneas, datos de entrega y folios, además del test básico original.
- `lintDebug`: correcto, sin errores ni incidencias fatales. Quedan 223 advertencias, principalmente textos que podrían extraerse a recursos, recursos originales sin uso y sugerencias de estilo/compatibilidad; no se desactivó ni se creó una baseline para ocultar errores.
- 55 XML de recursos analizados: sintaxis válida.
- Gradle Wrapper 9.5.0: checksum del paquete contrastado con el valor incluido en el proyecto.
- Catálogo: seis IDs únicos, imágenes existentes, precios en centavos y descuentos del 0 al 100.
- ZIP: se verifican integridad, estructura y ausencia de cachés, compilados, claves y rutas del SDK local.

## Límites de la verificación

No se ejecutó la app en un teléfono ni en un emulador. No se envió ningún mensaje por WhatsApp. La apariencia en distintos tamaños, el inicio de sesión en un teléfono, la persistencia tras cerrar el proceso y la apertura real de WhatsApp/WhatsApp Business se deben comprobar siguiendo LEEME.md. Las pruebas JVM verifican cálculos y contenido del resumen; no prueban la interfaz ni los Intents en un dispositivo.

El número del negocio no fue proporcionado y se deja configurable. Productos y precios son de demostración. No hay servidor, cobros, confirmación automática de recepción ni inventario remoto.
