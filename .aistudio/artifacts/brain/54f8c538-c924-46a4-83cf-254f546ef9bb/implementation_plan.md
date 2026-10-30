# Plan de Implementación — Rediseño Corporativo, Tienda Promocional y Documentación Modular (`Peñoles Conecta`)

## 1. Objetivo General y Alcance

Mejorar la identidad visual corporativa, legibilidad, accesibilidad (WCAG AA, TalkBack, escalado de fuente al 200 %, áreas táctiles de 48×48 dp, protección de teclado IME) y presentación de la tienda promocional sobre la aplicación Android existente **Peñoles Conecta** (`com.aistudio.penolesconecta.mxpqlz`, `namespace = "com.example"`), acompañada de una **auditoría de código (Code Review)** y **documentación modular `.md`** dentro de cada paquete (`data`, `feature/*`, `ui/theme`) más `ASSET_SOURCES.md` y `CODE_REVIEW_AND_ARCHITECTURE.md` en la raíz del proyecto.

Se conservan íntegramente:
- Kotlin, Jetpack Compose y Material 3 con las versiones actuales del proyecto.
- `package`, `applicationId`, navegación, firmas públicas, estados, `testTag`s, callbacks y lógica de negocio.
- Autenticación (credenciales de administrador `Matrix` / `Matrix123`, correo/contraseña y Google Sign-In), Firebase Firestore, Firebase AI Logic, App Check, Room y DataStore.
- IDs y SKUs de productos (`promo_termo_acero`, `promo_gorra_institucional`, `promo_libreta_ejecutiva`, `promo_playera_polo`, `promo_mochila_ejecutiva`) y catálogo industrial informativo separado.

---

## 2. Etapas de Implementación

### Etapa 1: Paleta, Tema Material 3 y Tipografía (`ui/theme/`)

1. **`Color.kt` y `Theme.kt`**:
   - Centralizar todos los tokens propuestos para Modo Claro y Modo Oscuro:
     - **Modo Claro**: `primary = #C41E24`, `onPrimary = #FFFFFF`, `primaryContainer = #FFE8E9`, `onPrimaryContainer = #7A1118`, `secondary = #475569`, `onSecondary = #FFFFFF`, `secondaryContainer = #E8EDF3`, `onSecondaryContainer = #17212F`, `background = #F5F6F8`, `onBackground = #171A21`, `surface = #FFFFFF`, `onSurface = #171A21`, `surfaceVariant = #EEF1F5`, `onSurfaceVariant = #5E6774`, `outline = #748093`, `outlineVariant = #D7DEE7`, `error = #B3261E`, `onError = #FFFFFF`, `errorContainer = #F9DEDC`, `onErrorContainer = #410E0B`.
     - **Modo Oscuro**: `primary = #FFB3B5`, `onPrimary = #3B080C`, `primaryContainer = #5D1114`, `onPrimaryContainer = #FFDAD8`, `secondary = #BEC8D6`, `onSecondary = #1B2635`, `secondaryContainer = #303C4C`, `onSecondaryContainer = #E0E8F3`, `background = #0E1117`, `onBackground = #F3F5F7`, `surface = #151922`, `onSurface = #F3F5F7`, `surfaceVariant = #202633`, `onSurfaceVariant = #AAB2C0`, `outline = #7D899D`, `outlineVariant = #30394A`, `error = #FFB4AB`, `onError = #690005`, `errorContainer = #93000A`, `onErrorContainer = #FFDAD6`.
   - Definir explícitamente los roles `surfaceContainerLowest`, `surfaceContainerLow`, `surfaceContainer`, `surfaceContainerHigh`, `surfaceContainerHighest` y `surfaceTint` en `lightColorScheme` y `darkColorScheme` para evitar tintes predeterminados ajenos a la paleta.
   - Conservar `#ED3237` únicamente como acento decorativo (`PenolesDecorativeAccent`), nunca como fondo de texto blanco pequeño.
   - Definir tokens semánticos verificados (éxito `#146C43` / `#6DD69B`, advertencia `#8A5300` / `#F2C166`, información `#1F5D99` / `#A9C7FF` con sus contenedores claros/oscuros) y un sistema de geometría compartida (`Shapes`: tarjetas `16.dp`, campos/botones `12.dp`, banners `24.dp` y márgenes adaptativos `16.dp` / `20.dp` / `24–32.dp`).
   - Mantener `dynamicColor = false` para preservar la identidad corporativa.

2. **`Type.kt` y Recursos Tipográficos**:
   - Auditar los archivos actuales en `app/src/main/res/font/` (`montserrat.ttf` cuyo nombre interno es *Montserrat Thin* estático y `plus_jakarta_sans.ttf`).
   - Descargar e instalar con `font-util` los pesos reales de **Montserrat** y **Plus Jakarta Sans** si están disponibles en la herramienta del entorno; en caso de que algún peso estático no esté disponible localmente, registrar solo los pesos verificados con fallback limpio a `FontFamily.SansSerif` sin duplicar un mismo archivo estático como falsos pesos `Normal`/`Medium`/`SemiBold`/`Bold`, documentando cualquier archivo faltante en `ASSET_SOURCES.md`.
   - Configurar la escala tipográfica exacta (sp tamaño / interlínea):
     - `headlineLarge`: Montserrat 700, `28.sp / 34.sp`
     - `headlineMedium`: Montserrat 600, `24.sp / 30.sp`
     - `titleLarge`: Montserrat 600, `20.sp / 26.sp`
     - `titleMedium`: Plus Jakarta Sans 600, `16.sp / 22.sp`
     - `bodyLarge`: Plus Jakarta Sans 400, `16.sp / 24.sp`
     - `bodyMedium`: Plus Jakarta Sans 400, `14.sp / 20.sp`
     - `labelLarge`: Plus Jakarta Sans 600, `14.sp / 20.sp`
     - `bodySmall` y `labelMedium`: Plus Jakarta Sans 400–500, `12.sp / 16.sp` (ningún texto funcional menor de `12.sp`, sin mayúsculas sostenidas forzadas).

---

### Etapa 2: Componentes Compartidos, Identidad e Imágenes (`feature/common/PenolesCommonComponents.kt`)

1. **Identificación Corporativa Textual (`CorporateTopBar`, `MainActivity`, `AuthScreen`)**:
   - Retirar el cuadro rojo con la letra `"P"` que simulaba un logotipo corporativo no autorizado.
   - Sustituirlo por una identificación institucional textual limpia (`PEÑOLES` / `Peñoles Conecta`) con acento lineal discreto mientras no exista el archivo vectorial/bitmap oficial autorizado, documentando el recurso pendiente en `ASSET_SOURCES.md`.
2. **Resolución Explícita de Imágenes (`PenolesImage`) y Placeholders Vectoriales Diferenciados**:
   - Retirar el uso de `R.drawable.img_promo_store_kit_1793310481110` (que contiene la marca ajena *"Global Solutions"*) de toda la presentación Peñoles.
   - Crear 5 `VectorDrawable` limpios y diferenciados por artículo sobre fondo neutro (`1080×1080` proporcional, `ContentScale.Fit` con margen interior) + placeholders separados para noticias/instalaciones y productos genéricos:
     - `promo_termo_acero` → placeholder vectorial de termo de acero (esperando `img_store_termo_penoles.webp`)
     - `promo_gorra_institucional` → placeholder vectorial de gorra estructurada (esperando `img_store_gorra_penoles.webp`)
     - `promo_libreta_ejecutiva` → placeholder vectorial de libreta ejecutiva (esperando `img_store_libreta_penoles.webp`)
     - `promo_playera_polo` → placeholder vectorial de playera tipo polo (esperando `img_store_polo_penoles.webp`)
     - `promo_mochila_ejecutiva` → placeholder vectorial de mochila para laptop (esperando `img_store_mochila_penoles.webp`)
   - Refactorizar `PenolesImage` manteniendo compatibilidad hacia atrás con parámetros opcionales (`productId: String? = null`, `isProduct: Boolean = false`):
     - Si `imageKeyOrUrl` es una URL `https://` o `http://` válida, cargarla con prioridad mediante `AsyncImage` (Coil) y usar el placeholder neutro correspondiente (nunca una foto minera para un producto).
     - Si es una clave local o se provee `productId`, resolver mediante un mapa explícito de claves/alias exactos (sin usar `contains("promo")` ni `contains("mining")`).
3. **Geometría y Accesibilidad en Componentes Comunes**:
   - `CategoryBadge`, `SectionHeader`, `EmptyStateCard` y `CorporateTopBar` con radios de `12.dp` / `16.dp`, áreas táctiles mínimas de `48.dp`, contraste verificado y sin `uppercase()` sostenido en textos largos.

---

### Etapa 3: Rediseño de Inicio y Tienda (`HomeScreen.kt` y `StoreAndCheckoutScreens.kt`)

1. **Inicio (`HomeScreen.kt`)**:
   - Márgenes horizontales adaptativos (`16.dp` en `< 360.dp`, `20.dp` en móvil estándar, `24–32.dp` en tablet).
   - Banner principal con relación `16:9`, radio `24.dp`, degradado oscuro localizado para garantizar contraste de texto blanco sobre fotografía real y textos renderizados 100 % en Compose.
   - Accesos rápidos, comunicados recientes, resumen de indicadores y productos destacados con tarjetas de radio `16.dp`, borde `outlineVariant` combinado con jerarquía tipográfica e iconos, sin bloques repetidos.
2. **Tienda Promocional y Catálogo Industrial (`StoreAndCheckoutScreens.kt`)**:
   - Aviso institucional permanentemente visible: **«Tienda de demostración · Sin cobros reales»**.
   - Cuadrícula adaptativa sensible al ancho y al tamaño de fuente del sistema (`LocalConfiguration.current.fontScale`):
     - 1 columna en pantallas estrechas (`< 360.dp`) o cuando `fontScale >= 1.3f`.
     - 2 columnas en móvil estándar (`360–599.dp`).
     - Múltiples columnas + panel maestro-detalle en tablet (`>= 600.dp`).
   - Tarjetas de producto (`PromoProductGridCard`) consistentes: contenedor cuadrado `1:1` con fondo neutro (`surfaceVariant`), `ContentScale.Fit` con margen interno, categoría legible, título de hasta 2 líneas (y nombre completo en accesibilidad y vista de detalle), precio demostrativo claro, botón de favorito (`48×48.dp` con estado accesible no dependiente solo del color) y botón de acción (`>= 52.dp` en acciones primarias).
   - Distinción clara de estados: seleccionado (icono + borde + contenedor), agotado (`stockLabel`), cargando, vacío y error.
   - Protección de insets de teclado (`imePadding()`) y barras del sistema en la hoja de carrito y formulario de checkout demostrativo.

---

### Etapa 4: Noticias, Asistente, Perfil, Acceso y Navegación (`NewsAndSearchScreens.kt`, `AssistantAndProfileScreens.kt`, `AuthScreen.kt`, `MainActivity.kt`)

1. **Noticias y Búsqueda (`NewsAndSearchScreens.kt`)**:
   - Jerarquía editorial clara (`headlineMedium` / `titleLarge` en Montserrat, cuerpo de lectura en `bodyLarge` Plus Jakarta Sans `16/24.sp`), metadatos honestos (categoría, fecha y fuente cuando existan) y placeholders editoriales separados de la tienda.
2. **Asistente y Perfil (`AssistantAndProfileScreens.kt`)**:
   - Campo de entrada del chat con `imePadding()` para que el teclado nunca tape la caja de mensaje ni el botón de envío.
   - Estados honestos de carga/envío y error con opción de reintento, burbujas legibles sobre superficies neutras y fuentes verificadas cuando existan.
   - En Perfil y Directorio, formularios alineados (`12.dp` en campos/botones, `52.dp` de altura mínima en botones primarios) y preferencias accesibles con TalkBack.
3. **Acceso (`AuthScreen.kt` y `LoginScreen.kt`) y `MainActivity.kt`**:
   - Alinear formularios, validaciones visibles con icono + texto (no solo color), conservar el acceso de administrador (`Matrix` / `Matrix123`), el flujo de correo/contraseña y el botón de Google Sign-In con su marca intacta.
   - En `MainActivity.kt`, actualizar el encabezado del `NavigationRail` para usar la identificación textual corporativa en lugar del emblema `"P"` inventado, manteniendo navegación, pestañas activas (icono + etiqueta + indicador) y `testTag`s.

---

### Etapa 5: Code Review Estricto y Documentación Modular `.md` para Agentes

Crear documentación estructurada y mantenible para que cualquier agente futuro comprenda contratos, arquitectura, reglas de diseño y activos pendientes, incluyendo una regla explícita de actualización continua:
- `ASSET_SOURCES.md` (raíz): inventario de recursos gráficos y tipográficos actuales, procedencia, licencias, activos retirados de UI (`img_promo_store_kit_1793310481110.jpg`) y lista exacta de archivos `.webp`, logotipo oficial y pesos tipográficos pendientes.
- `CODE_REVIEW_AND_ARCHITECTURE.md` (raíz): reporte del Code Review estricto (hallazgos corregidos de contraste WCAG, tipografía estática mal mapeada, acoplamiento de imágenes por subcadena, logo simulado, áreas táctiles e insets IME) y mapa general del sistema.
- Documentación modular por paquete:
  - `app/src/main/java/com/example/data/README.md` (contratos de modelos, repositorios, Room, DataStore, Firebase y reglas de no-modificación de esquemas/SKUs).
  - `app/src/main/java/com/example/ui/theme/README.md` (sistema de color claro/oscuro, escala tipográfica, formas y tokens semánticos).
  - `app/src/main/java/com/example/feature/common/README.md` (componentes compartidos, reglas de `PenolesImage` y `CorporateTopBar`).
  - `app/src/main/java/com/example/feature/home/README.md` (estructura de Inicio, jerarquía de banners y accesos).
  - `app/src/main/java/com/example/feature/store/README.md` (reglas de la tienda demostrativa, mapa de IDs → recursos `.webp` objetivo y separación del catálogo industrial).
  - `app/src/main/java/com/example/feature/news/README.md` (jerarquía editorial y buscador global).
  - `app/src/main/java/com/example/feature/assistant/README.md` (asistente corporativo, manejo de IME y perfil).
  - `app/src/main/java/com/example/feature/auth/README.md` (flujos de autenticación, `LoginViewModel` y credenciales de prueba).

---

### Etapa 6: Verificación y Pruebas

1. Ejecutar `compile_applet` tras completar las etapas para verificar compilación limpia sin errores.
2. Ejecutar `lint_applet` y pruebas unitarias/Robolectric disponibles (`gradle :app:testDebugUnitTest`) para validar que los `testTag`s, navegación, `LoginViewModel` y pantallas principales funcionan sin regresiones.
3. Entregar el resumen verificable con archivos modificados, recursos pendientes documentados y resultados reales de compilación, lint y tests.
