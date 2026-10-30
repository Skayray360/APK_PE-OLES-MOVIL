# Inventario de Recursos Visuales y Tipográficos (`ASSET_SOURCES.md`)

> **Regla de mantenimiento para Agentes**: Cualquier adición, reemplazo o retiro de recursos en `app/src/main/res/drawable*`, `app/src/main/res/mipmap*` o `app/src/main/res/font` **debe** actualizarse inmediatamente en este archivo.

---

## 1. Estado de Identidad Corporativa y Logotipo

| Recurso Objetivo | Estado Actual | Fallback Activo en Código | Acción / Autorización Pendiente |
| :--- | :--- | :--- | :--- |
| `res/drawable/ic_logo_penoles_oficial.xml` (o `.png` transparente de alta resolución + variante para fondo oscuro) | **PENDIENTE DE ARCHIVO OFICIAL AUTORIZADO** | `CorporateBrandIdentityBadge` en `PenolesCommonComponents.kt` muestra identificación textual limpia (`PEÑOLES` con barra vertical `#C41E24`), sin presentar una letra `"P"` ni un símbolo inventado como logotipo oficial. | Integrar únicamente el archivo vectorial (`VectorDrawable`) o PNG transparente oficial y autorizado por Comunicación Corporativa de Industrias Peñoles, respetando proporción y área libre. |
| `res/drawable/img_launcher_icon_1793310478285.jpg` y `res/mipmap-anydpi-v26/ic_launcher.xml` | **CONSERVADO SOLO COMO ICONO DE LAUNCHER TEMPORAL** | Adaptive icon existente (`ic_launcher_foreground.xml` / `ic_launcher_background.xml`). No se utiliza dentro de pantallas como logotipo oficial corporativo. | Sustituir por el símbolo corporativo oficial autorizado legible a tamaño pequeño cuando sea proporcionado. |

---

## 2. Fotografía Institucional y Editorial (`res/drawable`)

> **Nota normativa**: El hecho de que una fotografía sea pública en la web no implica licencia de redistribución comercial. Los recursos actuales operan como material visual demostrativo de referencia y requieren validación formal de derechos antes de distribución externa.

| Archivo en `res/drawable` | Uso en la Aplicación | Relación / Escala | Procedencia / Estado de Permiso |
| :--- | :--- | :--- | :--- |
| `img_banner_sustainability_1793310480917.jpg` | Banners y noticias de Sostenibilidad, Agua y Comunidad (`img_banner_sustainability`). | `16:9` en banners (`ContentScale.Crop` con degradado oscuro localizado para texto Compose). | Activo local del proyecto. **Permiso institucional de redistribución pendiente de validación por Comunicación Corporativa.** |
| `img_banner_mining_innovation_1793310478889.jpg` | Banners de Innovación, Seguridad y cabecera de Acceso (`img_banner_mining_innovation`). | `16:9` en banners (`ContentScale.Crop` con degradado oscuro localizado). | Activo local del proyecto. **Permiso institucional pendiente de validación.** |
| `img_industrial_silver_zinc_1793310481982.jpg` | Catálogo Industrial informativo (`ind_plata_afinada`, `ind_zinc_shg`, `ind_sulfato_sodio`, `ind_oxido_magnesio`) y nota de Simposio Metalúrgico (`img_industrial_silver_zinc`). | `ContentScale.Crop` en fichas técnicas industriales. | Activo local del proyecto. **Permiso institucional pendiente de validación.** |
| `ic_placeholder_news_editorial.xml` | Placeholder exclusivo para noticias y comunicados sin imagen válida. | `VectorDrawable` editorial neutro (`320×180`). | Recurso vectorial propio del proyecto (sin restricciones de terceros). |

---

## 3. Recursos de la Tienda Promocional (`StoreScreen` / `PenolesImage`)

### 3.1 Recurso Retirado de la Interfaz
* **`res/drawable/img_promo_store_kit_1793310481110.jpg`**:
  * **Motivo de retiro**: Contiene artículos promocionales con la marca ajena **«Global Solutions»** y se repetía en todos los artículos mediante `contains("promo")`.
  * **Estado**: **Desvinculado por completo de la capa de presentación (`PenolesImage` y `PenolesStoreAssetRegistry`)**. Ninguna pantalla ni tarjeta lo referencia.

### 3.2 Mapa Explícito de Productos Promocionales y Archivos `.webp` Pendientes

Especificación técnica requerida para cada archivo `.webp` definitivo cuando sea entregado:
* Dimensiones: `1080 × 1080 px` (`1:1`), formato **WebP** (`100–250 KB` orientativo sin degradar bordes).
* Fondo: Blanco o gris neutro de estudio, iluminación consistente, objeto completo con margen interior, renderizado con `ContentScale.Fit`.
* Marca: Artículo fotografiado real o mockup sin marca al que se componga posteriormente el archivo oficial autorizado del logotipo Peñoles (nunca texto improvisado por generador).

| `productId` (Firestore / Catálogo) | SKU Conservado | Recurso `.webp` Objetivo (Pendiente) | Placeholder Activo en `res/drawable` |
| :--- | :--- | :--- | :--- |
| `promo_termo_acero` | `PEN-PROMO-TRM-01` | `img_store_termo_penoles.webp` | `R.drawable.ic_placeholder_store_termo` (Vectorial neutro individual) |
| `promo_gorra_institucional` | `PEN-PROMO-GOR-02` | `img_store_gorra_penoles.webp` | `R.drawable.ic_placeholder_store_gorra` (Vectorial neutro individual) |
| `promo_libreta_ejecutiva` | `PEN-PROMO-LIB-03` | `img_store_libreta_penoles.webp` | `R.drawable.ic_placeholder_store_libreta` (Vectorial neutro individual) |
| `promo_playera_polo` | `PEN-PROMO-PLY-04` | `img_store_polo_penoles.webp` | `R.drawable.ic_placeholder_store_polo` (Vectorial neutro individual) |
| `promo_mochila_ejecutiva` | `PEN-PROMO-MOC-05` | `img_store_mochila_penoles.webp` | `R.drawable.ic_placeholder_store_mochila` (Vectorial neutro individual) |
| *(Cualquier otro producto promocional sin URL)* | — | — | `R.drawable.ic_placeholder_product_neutral` (Nunca usa fotos mineras) |

---

## 4. Recursos Tipográficos (`res/font` y `ui/theme/Type.kt`)

| Archivo en `res/font` | Metadatos OpenType Verificados (`name` / `OS/2` / `fvar`) | Estado en `Type.kt` | Archivos Faltantes Documentados |
| :--- | :--- | :--- | :--- |
| `res/font/montserrat.ttf` | Estático (`has_fvar = false`). `nameID 1 = "Montserrat Thin"`, `nameID 4 = "Montserrat Thin Regular"`. No corresponde a los pesos 600 (`SemiBold`) ni 700 (`Bold`) requeridos para titulares. | **No se duplica como falsos pesos 400/500/600/700.** Se usa `FontFamily.SansSerif` temporalmente en `MontserratFamily` para preservar pesos 600 y 700 reales. | Pendiente agregar `montserrat_semibold.ttf` (peso 600) y `montserrat_bold.ttf` (peso 700) —o fuente variable auténtica con tabla `fvar`— en `res/font/`. |
| `res/font/plus_jakarta_sans.ttf` | Estático (`has_fvar = false`). `nameID 1 = "Plus Jakarta Sans"`, `nameID 2 = "Regular"`, `usWeightClass = 400`. | Registrado únicamente como `FontWeight.Normal (400)` en `PlusJakartaSansRegularFamily`. Para pesos `500 (Medium)` y `600 (SemiBold)` se usa `FontFamily.SansSerif` (`PlusJakartaSansWeightedFamily`) evitando falsificar pesos con el mismo archivo estático. | Pendiente agregar `plus_jakarta_sans_medium.ttf` (peso 500) y `plus_jakarta_sans_semibold.ttf` (peso 600) en `res/font/`. |
