# Peñoles Conecta — Plataforma Móvil Android (Kotlin + Jetpack Compose)

Aplicación Android nativa inspirada en la comunicación institucional de **Industrias Peñoles** (`https://www.penoles.com.mx/`), diseñada para ofrecer noticias corporativas, tienda promocional demostrativa, catálogo industrial informativo con canal de cotización y el **Asistente Peñoles** impulsado por **Gemini (`gemini-3.8-flash`)** mediante **Firebase AI Logic** y recuperación aumentada (RAG).

---

## 1. Arquitectura y Tecnologías

* **Lenguaje e Interfaz**: Kotlin + Jetpack Compose + Material Design 3 (modo claro y modo oscuro centralizados en `Theme.kt`, tipografías `Montserrat` y `Plus Jakarta Sans` empaquetadas localmente en `res/font/`).
* **Arquitectura Limpia (MVVM + Offline-First)**:
  * `core/model`: Modelos tipados (`NewsArticle`, `Banner`, `Product`, `CartItem`, `FavoriteItem`, `Order`, `QuoteRequest`, `ChatMessage`, `UserProfile`).
  * `core/database`: Caché local offline con **Room** (`PenolesLocalDatabase`) para noticias consultadas e historial de búsquedas.
  * `core/datastore`: Preferencias locales con **Jetpack DataStore** (`UserPreferencesRepository`).
  * `core/security`: Inicialización de **Firebase App Check** (`configureAppCheck(context, intent)`).
  * `data/repository`: Repositorio en la nube con **Cloud Firestore** (`PenolesCloudRepository`) y manejo estructurado de errores (`handleFirestoreError`).
  * `feature/auth`: Autenticación mediante **Google Sign-In** con **Jetpack Credential Manager** (`GetSignInWithGoogleOption` y `GetGoogleIdOption`) y modo de lectura local de noticias sin sesión.
  * `feature/home`, `feature/news`, `feature/store`, `feature/assistant`: Módulos de interfaz Compose para las 5 secciones principales.

---

## 2. Reglas de Seguridad y Contratos de Backend (Firestore)

* El esquema intermedio está documentado en `firebase-blueprint.json` y las reglas de seguridad de confianza cero en `firestore.rules` (verificadas con `firestore.test.js` y `PenolesCloudRepositoryRuleTest.kt`).
* **Validación de Totales**: La colección `/orders/{orderId}` valida en las reglas de seguridad que `total == subtotal + shipping`, `subtotal > 0`, `shipping >= 0` e `isDemoOrder == true`.
* **Separación Comercial**:
  * **Modalidad A (Artículos Promocionales)**: Permite carrito (`/users/{userId}/cart_items/{itemId}`) y flujo de pedido en modo demostración claramente identificado.
  * **Modalidad B (Catálogo Industrial)**: Funciona exclusivamente como catálogo técnico informativo (Plata 99.99%, Zinc SHG 99.995%, Sulfato de Sodio, Óxido de Magnesio) y canal de solicitud de cotización (`/quote_requests/{quoteId}`).

---

## 3. Ejecución y Pruebas

1. **Compilación**: Ejecutar `./gradlew assembleDebug` o `gradle assembleDebug` en Android Studio.
2. **Pruebas de Reglas en JavaScript (Tier 1)**:
   ```bash
   FIRESTORE_EMULATOR_HOST="127.0.0.1:8085" node --test firestore.test.js
   ```
3. **Pruebas de Integración Robolectric (Tier 2)**:
   ```bash
   gradle :app:testDebugUnitTest --tests '*Rule*'
   ```
