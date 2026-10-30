# Paquete `com.example.feature.common` — Componentes Compartidos y Resolución de Imágenes

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se modifique `PenolesCommonComponents.kt` o el registro de activos `PenolesStoreAssetRegistry`.

## 1. Componentes Principales (`PenolesCommonComponents.kt`)

* `AdaptiveWindowBox` / `AdaptiveWindowInfo`:
  * Calcula puntos de corte responsivos y escala de fuente (`fontScale`):
    * Móvil estrecho (`< 360.dp`): margen horizontal `16.dp`.
    * Móvil normal (`360..599.dp`): margen horizontal `20.dp`.
    * Tablet (`600..839.dp`): margen horizontal `24.dp`.
    * Tablet expandida (`>= 840.dp`): margen horizontal `32.dp`.
* `CorporateBrandIdentityBadge` y `CorporateTopBar`:
  * Muestra la identificación institucional textual temporal (`PEÑOLES` con barra de acento `#C41E24`) mientras no exista el archivo oficial autorizado del logotipo corporativo.
  * Mantiene los `testTag`s: `top_bar_search_button`, `top_bar_notifications_button`, `top_bar_cart_button` con áreas táctiles de `48×48.dp`.
* `PenolesImage` y `PenolesStoreAssetRegistry`:
  * **Prioridad 1**: URLs remotas válidas (`https://` o `http://`) cargadas con `AsyncImage` (Coil).
  * **Prioridad 2**: Mapa explícito por `productId` (`promo_termo_acero`, `promo_gorra_institucional`, `promo_libreta_ejecutiva`, `promo_playera_polo`, `promo_mochila_ejecutiva`) hacia sus placeholders vectoriales neutros e individuales, preparados para recibir los archivos `.webp` objetivo (`img_store_*_penoles.webp`).
  * **Prioridad 3**: Mapa explícito de claves locales y alias históricos (`img_banner_sustainability`, `img_banner_mining_innovation`, `img_industrial_silver_zinc`).
  * **Separación estricta de fallbacks**: `ic_placeholder_product_neutral` para artículos de tienda e `ic_placeholder_news_editorial` para noticias (nunca se usa una foto minera ni `img_promo_store_kit_1793310481110.jpg` para sustituir un producto).
* `DemoStoreNoticeBanner`:
  * Aviso reutilizable «Tienda de demostración · Sin cobros reales».
