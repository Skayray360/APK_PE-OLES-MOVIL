# Paquete `com.example.ui.theme` — Sistema de Diseño, Paleta y Tipografía

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se ajusten colores, formas o estilos tipográficos en `Color.kt`, `Theme.kt` o `Type.kt`.

## 1. Archivos del Paquete

* `Color.kt`:
  * Centraliza la propuesta cromática para Modo Claro y Modo Oscuro con contraste WCAG AA verificado:
    * **Modo Claro**: `primary = #C41E24` (`onPrimary = #FFFFFF`, contraste `~5.90:1`), `primaryContainer = #FFE8E9`, `onPrimaryContainer = #7A1118`, `secondary = #475569`, `background = #F5F6F8`, `surface = #FFFFFF`, `surfaceVariant = #EEF1F5`, `onSurface = #171A21`, `onSurfaceVariant = #5E6774`, `outline = #748093`, `outlineVariant = #D7DEE7`, `error = #B3261E`.
    * **Modo Oscuro**: `primary = #FFB3B5` (`onPrimary = #3B080C`), `primaryContainer = #5D1114`, `onPrimaryContainer = #FFDAD8`, `secondary = #BEC8D6`, `background = #0E1117`, `surface = #151922`, `surfaceVariant = #202633`, `onSurface = #F3F5F7`, `onSurfaceVariant = #AAB2C0`, `outline = #7D899D`, `outlineVariant = #30394A`, `error = #FFB4AB`.
    * **Acentos Semánticos (`PenolesSemanticColors`)**: Éxito (`#146C43` / `#6DD69B`), Advertencia (`#8A5300` / `#F2C166`) e Información (`#1F5D99` / `#A9C7FF`).
    * **Acento Decorativo**: `PenolesDecorativeRed = #ED3237` (nunca usar como fondo de texto blanco pequeño).
* `Theme.kt`:
  * Define `LightColorScheme` y `DarkColorScheme` incluyendo todos los roles `surfaceContainerLowest..Highest` y `surfaceTint` para evitar tintes rosados automáticos de Material 3.
  * Mantiene `dynamicColor = false` por defecto.
  * Define `PenolesShapes`: campos/botones `12.dp`, tarjetas `16.dp`, banners `24.dp`.
* `Type.kt`:
  * Escala tipográfica corporativa (`headlineLarge` 28/34, `headlineMedium` 24/30, `titleLarge` 20/26, `titleMedium` 16/22, `bodyLarge` 16/24, `bodyMedium` 14/20, `labelLarge` 14/20, `bodySmall` y `labelMedium` 12/16).
  * No duplica archivos `.ttf` estáticos como pesos falsos (ver `ASSET_SOURCES.md`).
