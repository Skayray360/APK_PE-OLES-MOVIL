# Paquete `com.example.feature.news` — Sala de Prensa y Búsqueda Global (`NewsAndSearchScreens.kt`)

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se modifique `NewsAndSearchScreens.kt`.

## 1. Pantallas y Jerarquía Editorial

* `NewsScreen`:
  * Buscador editorial (`news_search_input`) con radio `12.dp` y fila de categorías (`news_categories_filter_row`) con `FilterChip`s que incluyen icono `Check` al seleccionarse.
  * Lista vertical en móvil y cuadrícula adaptativa en tablet (`news_articles_list`), con tarjetas `EditorialNewsCard` (`news_article_card_${article.id}`) de radio `16.dp` y borde `outlineVariant`.
  * **Metadatos honestos**: Muestra fuente (`article.author`), tiempo de lectura (`readingTimeMinutes`) y fecha únicamente cuando `article.createdAt != null` (no inventa fechas estáticas).
* `NewsDetailScreen`:
  * Lector editorial con título `headlineMedium` (Montserrat 600, 24/30 sp), resumen destacado y cuerpo de lectura en `bodyLarge` (Plus Jakarta Sans 400, 16/24 sp).
* `GlobalSearchScreen`:
  * Buscador transversal (`global_search_textfield`) sobre noticias, artículos promocionales (con miniatura individual `PenolesImage(productId = prod.id, isProduct = true)`), catálogo industrial y preguntas frecuentes, protegido con `.imePadding()`.

## 2. `testTag`s Conservados

* `news_search_input`, `news_categories_filter_row`, `news_articles_list`
* `news_article_card_${article.id}`, `news_detail_back_button`, `news_detail_scroll`
* `global_search_textfield`
