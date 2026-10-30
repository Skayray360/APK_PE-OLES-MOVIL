# Paquete `com.example.core` — Modelos, Persistencia Local (Room/DataStore) y Seguridad

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se modifique algún modelo de datos, entidad Room, preferencia DataStore o configuración de seguridad en `core/`.

## 1. Archivos del Paquete

* `model/PenolesModels.kt`:
  * Modelos de dominio anotados para Firestore (`@IgnoreExtraProperties`, `@DocumentId`, `@ServerTimestamp`):
    * `UserProfile`, `NewsArticle`, `Banner`, `Product`, `CartItem`, `FavoriteItem`, `Order`, `QuoteRequest`, `ChatMessage`, `ChatAction`, `CorporateFaq`.
  * `PenolesReferenceCatalog`: Datos semilla institucionales para noticias, banners, preguntas frecuentes y catálogo de productos (5 artículos promocionales + 4 productos industriales).
* `database/PenolesLocalDatabase.kt`:
  * Base de datos SQLite local mediante **Room** (`PenolesLocalDao`) que almacena en caché noticias, productos y el historial local de búsquedas para soporte offline y respuesta inmediata.
* `datastore/UserPreferencesRepository.kt`:
  * Repositorio **Preferences DataStore** (`LocalUserPreferences`) que persiste `darkThemeEnabled`, `autoAdvanceCarousel`, `pushNotificationsConsent`, `subscribedNotificationTopics` y la sesión local activa (`activeSessionUsername`, `activeSessionRole`, `activeSessionEmail`).
* `security/AppCheckInitializer.kt`:
  * Función `configureAppCheck(activity, intent)` que registra el proveedor de Firebase App Check antes de inicializar lecturas o escrituras remotas.

## 2. Reglas de Invarianza

1. **No alterar IDs ni SKUs**: Los `id` de `PenolesReferenceCatalog.referenceProducts` (`promo_termo_acero`, `promo_gorra_institucional`, `promo_libreta_ejecutiva`, `promo_playera_polo`, `promo_mochila_ejecutiva`, `ind_plata_afinada`, `ind_zinc_shg`, `ind_sulfato_sodio`, `ind_oxido_magnesio`) forman parte del contrato con Firestore, el carrito y la capa de resolución de imágenes.
2. **No migrar esquemas Room ni Firestore por cambios visuales**: La resolución de activos visuales por producto ocurre exclusivamente en la capa de presentación (`PenolesCommonComponents.kt`).
