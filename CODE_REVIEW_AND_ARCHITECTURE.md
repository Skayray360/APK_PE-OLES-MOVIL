# Auditoría de Código (Code Review) y Arquitectura General — `Peñoles Conecta`

> **DIRECTRIZ OBLIGATORIA PARA AGENTES DE IA**:
> Este documento y los archivos `README.md` modulares dentro de cada paquete (`core/`, `data/`, `ui/theme/`, `feature/*`) constituyen la memoria arquitectónica viva del sistema. **Cualquier agente que realice modificaciones en el código DEBE actualizar este archivo y el `README.md` del paquete afectado dentro del mismo turno de trabajo.**

---

## 1. Reporte de Code Review Estricto (Filtros de Mejores Prácticas y Normativas)

Se auditó el proyecto completo bajo 6 filtros: **Contraste y Accesibilidad (WCAG 2.1 AA)**, **Integridad de Marca**, **Tipografía OpenType**, **Arquitectura y Acoplamiento**, **Seguridad / Privacidad**, y **Adaptabilidad Multidispositivo**.

### 1.1 Hallazgos Críticos Corregidos

| ID | Categoría | Hallazgo Detectado en Código Previo | Corrección Aplicada |
| :--- | :--- | :--- | :--- |
| **CR-01** | **Contraste WCAG AA** | `Color.kt` y `Theme.kt` usaban `#ED3237` como `primary` en modo claro y oscuro con texto blanco (`Color.White`), dando un contraste de `~4.11:1` (inferior al mínimo `4.5:1` para texto normal). En modo oscuro, `Color.White` sobre controles primarios o fondos claros reducía la legibilidad. | Se actualizó `LightPrimary = #C41E24` (`~5.90:1` con `#FFFFFF`) y `DarkPrimary = #FFB3B5` con `DarkOnPrimary = #3B080C`. Se reemplazaron usos de `Color.White` sobre `primary` por `MaterialTheme.colorScheme.onPrimary` y `onPrimaryContainer`. `#ED3237` se conserva solo como token decorativo (`PenolesDecorativeRed`). |
| **CR-02** | **Tipografía OpenType (`Type.kt`)** | `Type.kt` registraba el mismo archivo estático `R.font.montserrat` y `R.font.plus_jakarta_sans` cuatro veces (`Normal`, `Medium`, `SemiBold`, `Bold`). Además, la inspección binaria de `montserrat.ttf` demostró que su tabla `name` interna es `"Montserrat Thin Regular"` (estático, sin eje `fvar`). Asimismo, `labelSmall` tenía `11.sp`. | Se eliminó la duplicación artificial de archivos estáticos. `plus_jakarta_sans.ttf` se registra exclusivamente en `FontWeight.Normal (400)` y los pesos `500`/`600`/`700` usan `FontFamily.SansSerif` mientras se integran los binarios `semibold`/`bold` reales. Ningún estilo funcional baja de `12.sp / 16.sp`. |
| **CR-03** | **Integridad de Marca (Tienda)** | `PenolesImage` asignaba `R.drawable.img_promo_store_kit_1793310481110` a cualquier cadena con `contains("promo")`. Dicha imagen contiene productos con la marca ajena **«Global Solutions»** e incluso podía interceptar URLs remotas que contuvieran la subcadena `"promo"`. | Se retiró `img_promo_store_kit_1793310481110.jpg` de la UI. `PenolesImage` ahora valida primero URLs `https://`/`http://` con prioridad vía Coil (`AsyncImage`) y resuelve claves locales e IDs de producto mediante `PenolesStoreAssetRegistry` con 5 placeholders vectoriales neutros e individuales. |
| **CR-04** | **Identidad Corporativa (Logo)** | `CorporateTopBar` y el encabezado del `NavigationRail` en `MainActivity.kt` dibujaban un recuadro rojo con una letra `"P"` tipográfica simulando ser el logotipo de Peñoles. | Se reemplazó por `CorporateBrandIdentityBadge`, una identificación textual institucional limpia (`PEÑOLES`) mientras se incorpora el archivo oficial autorizado (`ASSET_SOURCES.md`). |
| **CR-05** | **Ergonomía, Teclado (IME) y Escalado 200 %** | `AssistantScreen` y `CartAndCheckoutScreen` no aplicaban `imePadding()`, permitiendo que el teclado cubriera el campo de mensaje o el pie de compra. Las tarjetas de tienda usaban alturas fijas que truncaban controles con fuente al 200 %. | Se añadió `.imePadding()` en `AssistantScreen`, `CartAndCheckoutScreen`, `GlobalSearchScreen` y `LoginScreen`. La cuadrícula de Tienda cambia automáticamente a 1 columna cuando `maxWidth < 360.dp` o `fontScale >= 1.3f`. Los botones primarios usan `heightIn(min = 52.dp)` y áreas táctiles mínimas de `48×48.dp`. |
| **CR-06** | **Accesibilidad Semántica y No-Dependencia del Color** | `FilterChip`, pestañas y mensajes de error dependían principalmente del cambio de color, y `NewsScreen`/`HomeScreen` mostraban fechas fijas inventadas (`"Octubre 2026"`). | Se añadieron iconos de estado (`Icons.Default.Check` en chips/pestañas seleccionadas, `Icons.Default.ErrorOutline` en errores, `Icons.Default.CheckCircle` en disponibilidad), bordes explícitos `outlineVariant` y metadatos honestos (fecha solo cuando `createdAt != null`). |

---

## 2. Mapa de Estructura y Documentación Modular para Agentes

Cada paquete del proyecto contiene su propio archivo `README.md` con contratos, componentes, `testTag`s y reglas de negocio:

```text
/
├── ASSET_SOURCES.md                                           # Inventario de recursos gráficos, fuentes y permisos pendientes
├── CODE_REVIEW_AND_ARCHITECTURE.md                            # Este documento: Auditoría de código y mapa general
└── app/src/main/java/com/example/
    ├── MainActivity.kt                                        # Punto de entrada, App Check, NavHost y carcasa adaptativa
    ├── core/
    │   ├── README.md                                          # Documentación de modelos, Room, DataStore y seguridad
    │   ├── database/PenolesLocalDatabase.kt                   # Caché local Room (noticias, productos, búsquedas)
    │   ├── datastore/UserPreferencesRepository.kt             # Preferencias DataStore y sesión local
    │   ├── model/PenolesModels.kt                             # Modelos de dominio, Firestore @DocumentId y catálogo semilla
    │   └── security/AppCheckInitializer.kt                    # Inicialización de Firebase App Check
    ├── data/
    │   ├── README.md                                          # Documentación de capa de datos, Firestore y RAG + Gemini
    │   └── repository/PenolesCloudRepository.kt               # Sincronización Firestore y Asistente IA (Firebase AI Logic)
    ├── ui/theme/
    │   ├── README.md                                          # Documentación de paleta, contraste WCAG, tipografía y formas
    │   ├── Color.kt                                           # Tokens cromáticos Claro/Oscuro y colores semánticos
    │   ├── Theme.kt                                           # Esquemas Material 3, surfaceContainer* y PenolesShapes
    │   └── Type.kt                                            # Escala tipográfica Montserrat / Plus Jakarta Sans
    └── feature/
        ├── common/
        │   ├── README.md                                      # Documentación de componentes compartidos y PenolesImage
        │   └── PenolesCommonComponents.kt                     # CorporateTopBar, PenolesImage, AdaptiveWindowBox, badges
        ├── home/
        │   ├── README.md                                      # Documentación de pantalla de Inicio
        │   └── HomeScreen.kt                                  # Banner 16:9, accesos rápidos, noticias y tienda destacada
        ├── store/
        │   ├── README.md                                      # Documentación de Tienda Promocional y Catálogo Industrial
        │   └── StoreAndCheckoutScreens.kt                     # Cuadrícula adaptativa, detalle, cotización y checkout demo
        ├── news/
        │   ├── README.md                                      # Documentación de Sala de Prensa y Búsqueda Global
        │   └── NewsAndSearchScreens.kt                        # Listado editorial, lector de artículos y buscador global
        ├── assistant/
        │   ├── README.md                                      # Documentación de Asistente IA y Perfil/CMS
        │   └── AssistantAndProfileScreens.kt                  # Chat RAG + Gemini con protección IME, Perfil y CMS RBAC
        ├── auth/
        │   ├── README.md                                      # Documentación de Autenticación y LoginViewModel
        │   └── AuthScreen.kt                                  # Login ('Matrix'/'Matrix123'), Google Sign-In y lector invitado
        └── main/
            ├── README.md                                      # Documentación del ViewModel coordinador principal
            └── PenolesMainViewModel.kt                        # Orquestación de flujos entre Room, DataStore y Firestore
```

---

## 3. Contratos Inmutables del Sistema (No Modificar sin Instrucción Explícita)

1. **Identificadores de Aplicación**:
   - `namespace = "com.example"`
   - `applicationId = "com.aistudio.penolesconecta.mxpqlz"`
2. **IDs y SKUs de Productos Promocionales e Industriales**:
   - Promocionales: `promo_termo_acero` (`PEN-PROMO-TRM-01`), `promo_gorra_institucional` (`PEN-PROMO-GOR-02`), `promo_libreta_ejecutiva` (`PEN-PROMO-LIB-03`), `promo_playera_polo` (`PEN-PROMO-PLY-04`), `promo_mochila_ejecutiva` (`PEN-PROMO-MOC-05`).
   - Industriales: `ind_plata_afinada` (`PEN-IND-AG-9999`), `ind_zinc_shg` (`PEN-IND-ZN-SHG`), `ind_sulfato_sodio` (`PEN-IND-NA2SO4`), `ind_oxido_magnesio` (`PEN-IND-MGO`).
3. **Credenciales Administrativas de Prueba (`LoginViewModel`)**:
   - Usuario: `Matrix` / Contraseña: `Matrix123` → Rol `ADMINISTRADOR`.
4. **Seguridad y Backend**:
   - Mantener `configureAppCheck(this, intent)` antes de cualquier llamada a Firebase en `MainActivity.onCreate()`.
   - No relajar `firestore.rules` ni habilitar pagos reales en la tienda de demostración.
