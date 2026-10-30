# Paquete `com.example.feature.auth` — Autenticación y Acceso (`AuthScreen.kt`)

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se modifique `AuthScreen.kt` o `LoginViewModel`.

## 1. Componentes y Credenciales

* `LoginViewModel` y `LoginUiState`:
  * Valida las credenciales administrativas solicitadas (`Usuario: Matrix`, `Contraseña: Matrix123`) mediante `validateCredentials()`, asignando el rol `ADMINISTRADOR` y navegando hacia la pantalla de Inicio (`ROUTE_HOME`).
  * Incluye `autofillAdminCredentials()` para facilitar pruebas rápidas y automatizadas.
* `onGoogleSignInClicked` y `signOutUser`:
  * Integración con **Jetpack Credential Manager** (`GetSignInWithGoogleOption`) y `FirebaseAuth`.
* `LoginScreen`:
  * Diseño responsivo canónico: 2 columnas en tablets/pantallas anchas (`>= 680.dp`) y columna vertical fluida en móviles (`widthIn(max = 520.dp)`), protegido con `.imePadding()`.
  * Utiliza `CorporateBrandIdentityBadge(compact = true)` sin símbolos ni logotipos inventados.
  * Los errores de validación (`login_error_card`) incluyen icono `ErrorOutline` + borde + texto descriptivo.

## 2. `testTag`s Conservados

* `autofill_admin_chip`
* `username_input`, `password_input`, `toggle_password_visibility_button`
* `login_error_card`, `login_submit_button`
* `google_sign_in_button`, `guest_news_reader_button`
