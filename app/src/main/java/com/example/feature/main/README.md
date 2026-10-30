# Paquete `com.example.feature.main` — Coordinación de Estado (`PenolesMainViewModel.kt`)

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se modifique `PenolesMainViewModel.kt`.

## 1. Responsabilidades

* Orquesta la sincronización entre:
  * **Cloud Firestore** (`PenolesCloudRepository`) para datos en tiempo real de banners, noticias, catálogo, carrito, favoritos, órdenes demostrativas y cotizaciones industriales.
  * **Room** (`PenolesLocalDao`) para respaldo offline y búsqueda local instantánea (`GlobalSearchResults`).
  * **DataStore** (`UserPreferencesRepository`) para preferencias de tema claro/oscuro, carrusel y notificaciones.
* Expone `StateFlow`s inmutables consumidos con `collectAsStateWithLifecycle()` en `MainActivity.kt`.
* Gestiona el estado del Asistente IA (`AssistantUiState.Idle`, `Processing`, `Error`) y el historial de conversación en memoria durante la sesión.
