# Paquete `com.example.data` — Repositorio Cloud (Firestore + Firebase AI Logic)

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se modifique la lógica de sincronización en `PenolesCloudRepository.kt`, colecciones Firestore o integración con Gemini.

## 1. Archivos del Paquete

* `repository/PenolesCloudRepository.kt`:
  * Gestiona la lectura en tiempo real (`callbackFlow` + `addSnapshotListener`) y escritura transaccional en **Cloud Firestore** para:
    * `banners`, `news`, `products`, `users/{userId}`, `users/{userId}/cart`, `users/{userId}/favorites`, `orders` y `quotes`.
  * Integra **Firebase AI Logic** (`Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel("gemini-2.5-flash")`) combinando recuperación de contexto local verificado (RAG sobre `NewsArticle`, `Product` y `CorporateFaq`) con generación de respuestas en español y citas de fuentes institucionales.

## 2. Reglas de Seguridad y Operación

1. **Aislamiento por Usuario y Rol**:
   * Las colecciones de carrito y favoritos pertenecen al usuario autenticado.
   * Cuando el usuario ingresa mediante la sesión local de demostración (`local_matrix`), el repositorio opera de forma segura con estado en memoria / caché local sin lanzar excepciones de permisos en Firestore.
2. **Privacidad en Logs**:
   * Nunca registrar tokens de sesión, credenciales ni correos electrónicos personales en `Log.d` / `Log.e`.
3. **Modelo de IA**:
   * Conservar el modelo configurado (`gemini-2.5-flash`) y el fallback determinista RAG cuando la red no esté disponible.
