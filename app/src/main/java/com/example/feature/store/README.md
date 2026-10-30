# Paquete `com.example.feature.store` — Tienda Promocional y Catálogo Industrial (`StoreAndCheckoutScreens.kt`)

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se modifique `StoreAndCheckoutScreens.kt`.

## 1. Pantallas y Reglas de Negocio

* `StoreScreen`:
  * Separa mediante pestañas (`store_modality_tabs`) dos modalidades estrictamente distintas:
    1. **Artículos Promocionales (`tab_promotional_store`)**: Tienda de demostración con carrito, precios demostrativos y aviso permanente **«Tienda de demostración · Sin cobros reales»**.
    2. **Catálogo Industrial (`tab_industrial_catalog`)**: Exclusivamente informativo (Plata Afinada, Zinc SHG, Sulfato de Sodio, Óxido de Magnesio) con solicitud de cotización empresarial y sin compra directa.
  * **Cuadrícula Adaptativa Sensible a la Fuente**:
    * 1 columna (`GridCells.Fixed(1)`) cuando `maxWidth < 360.dp` o `fontScale >= 1.3f`.
    * 2 columnas (`GridCells.Fixed(2)`) en móvil estándar (`360..599.dp`).
    * Multicolumna adaptativa (`GridCells.Adaptive`) en tablet (`>= 600.dp`).
  * **Tarjetas de Producto (`StoreGridProductCard`)**:
    * Contenedor cuadrado `1:1` (`aspectRatio(1f)`) con fondo neutro `surfaceVariant` y `ContentScale.Fit` con margen interior de `16.dp` para mostrar el objeto completo sin recortes.
    * Botón de favorito de `48×48.dp` sobre superficie con borde para garantizar contraste tanto en modo claro como oscuro.
    * Estado de disponibilidad con icono (`CheckCircle` / `ErrorOutline`) + texto.
* `ProductDetailScreen`:
  * Muestra el nombre completo del artículo sin truncar, variantes seleccionables con icono `Check`, selector de cantidad con botones de `48×48.dp` y botón primario de `52.dp` de altura mínima (`add_to_cart_button` / `request_industrial_quote_button`).
* `CartAndCheckoutScreen`:
  * Flujo demostrativo de 4 pasos protegido con `.imePadding()` para que el teclado nunca tape el formulario de entrega ni el pie de confirmación (`checkout_continue_button`).

## 2. `testTag`s Conservados

* `store_modality_tabs`, `tab_promotional_store`, `tab_industrial_catalog`
* `store_search_input`, `store_products_grid`, `store_open_cart_banner_button`
* `store_product_card_${product.id}`
* `product_detail_back_button`, `request_industrial_quote_button`, `add_to_cart_button`
* `checkout_continue_button`
