# Paquete `com.example.feature.home` — Pantalla de Inicio (`HomeScreen.kt`)

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se modifique `HomeScreen.kt`.

## 1. Estructura y Responsabilidades

* **Cabecera contenida (`CorporateTopBar`)**: Sin duplicar bloques de bienvenida repetitivos.
* **Carrusel de Banner Principal (`HeroBannerCarousel`)**:
  * Relación `16:9` en móvil (`aspectRatio(16f / 9f)`), radio `24.dp`, degradado oscuro localizado para garantizar contraste de texto blanco sobre fotografía institucional y textos 100 % en Compose.
  * Indicadores de página diferenciados por forma/ancho (`28.dp` vs `8.dp`) y borde, no solo color.
* **Accesos Rápidos (`quick_categories_row`)**:
  * Chips con radio `12.dp`, altura mínima `48.dp`, borde `outlineVariant` e icono Material coherente.
* **Noticias Recientes**:
  * Lista vertical en móvil y cuadrícula de 2–3 columnas en tablet (`home_news_card_${article.id}`).
* **Productos Destacados (`home_store_carousel`)**:
  * Muestra el aviso «Tienda de demostración · Sin cobros reales» y renderiza cada producto con `PenolesImage(productId = product.id, isProduct = true, contentScale = ContentScale.Fit)` sobre contenedor neutro `surfaceVariant`.
* **Acceso al Asistente (`home_assistant_cta_card`)**:
  * Tarjeta institucional de radio `16.dp` hacia el módulo de consulta IA.

## 2. `testTag`s Conservados

* `home_lazy_column`
* `home_hero_carousel`
* `quick_categories_row`
* `home_news_card_${article.id}`
* `home_store_carousel`
* `home_assistant_cta_card`
