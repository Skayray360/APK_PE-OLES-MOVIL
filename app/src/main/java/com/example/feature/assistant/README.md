# Paquete `com.example.feature.assistant` — Asistente IA y Perfil/CMS (`AssistantAndProfileScreens.kt`)

> **Regla de mantenimiento para Agentes**: Actualiza este documento cada vez que se modifique `AssistantAndProfileScreens.kt`.

## 1. Pantallas y Comportamiento

* `AssistantScreen`:
  * Interfaz conversacional conectada a `PenolesMainViewModel.askAssistant` (RAG local + Firebase AI Logic Gemini).
  * **Protección de Teclado (IME)**: El contenedor principal aplica `.imePadding()` para garantizar que el campo `assistant_input_field` y el botón `assistant_send_button` (`48×48.dp`) permanezcan siempre visibles sobre el teclado virtual.
  * **Burbujas y Estados Honestos**:
    * Mensajes del usuario con `MaterialTheme.colorScheme.primary` y `onPrimary` (alto contraste tanto en modo claro como oscuro).
    * Respuestas del asistente sobre `surface` con borde `outlineVariant`, mostrando fuentes verificadas (`citations`) únicamente cuando existen.
    * Estados explícitos para `AssistantUiState.Processing` y `AssistantUiState.Error`.
* `ProfileAndAdminScreen`:
  * Diseño adaptativo (1 columna en móvil, tarjetas superiores lado a lado en tablet `>= 700.dp`).
  * Incluye identidad y rol RBAC (`ADMINISTRADOR` / `USUARIO_ESTANDAR`), acceso al CMS (`open_cms_admin_button`), favoritos, historial de pedidos demostrativos, cotizaciones industriales, preferencias (`dark_theme_switch`, avance de carrusel, consentimiento de notificaciones Android 13+) y cierre de sesión (`sign_out_button`).

## 2. `testTag`s Conservados

* `assistant_suggested_prompts_row`, `assistant_messages_list`, `assistant_input_field`, `assistant_send_button`
* `profile_lazy_column`, `open_cms_admin_button`, `dark_theme_switch`, `sign_out_button`
