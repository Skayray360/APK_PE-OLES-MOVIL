package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Propuesta de paleta cromática para la interfaz de Peñoles Conecta.
 * Nota: Estos valores son una propuesta de diseño UI con contraste WCAG AA verificado,
 * no una declaración de colores oficiales de un manual corporativo.
 *
 * Referencia de contraste sobre texto blanco pequeño:
 * - Blanco (#FFFFFF) sobre #ED3237 = ~4.11:1 (insuficiente para texto pequeño < 18pt).
 * - Blanco (#FFFFFF) sobre #C41E24 = ~5.90:1 (cumple holgadamente WCAG AA >= 4.5:1).
 */

// Acento decorativo existente del proyecto (no usar como fondo de texto blanco pequeño)
val PenolesDecorativeRed = Color(0xFFED3237)
val PenolesRed = PenolesDecorativeRed

// ============================================================================
// MODO CLARO — Superficies neutras industriales y rojo controlado
// ============================================================================
val LightPrimary = Color(0xFFC41E24)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFFFE8E9)
val LightOnPrimaryContainer = Color(0xFF7A1118)

val LightSecondary = Color(0xFF475569)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFE8EDF3)
val LightOnSecondaryContainer = Color(0xFF17212F)

val LightTertiary = Color(0xFF334155)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFE2E8F0)
val LightOnTertiaryContainer = Color(0xFF0F172A)

val LightBackground = Color(0xFFF5F6F8)
val LightOnBackground = Color(0xFF171A21)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF171A21)
val LightSurfaceVariant = Color(0xFFEEF1F5)
val LightOnSurfaceVariant = Color(0xFF5E6774)

val LightOutline = Color(0xFF748093)
val LightOutlineVariant = Color(0xFFD7DEE7)

val LightError = Color(0xFFB3261E)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFF9DEDC)
val LightOnErrorContainer = Color(0xFF410E0B)

// Contenedores neutros de superficie (Modo Claro) para evitar tintes rosados por defecto
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF8F9FB)
val LightSurfaceContainer = Color(0xFFF2F4F8)
val LightSurfaceContainerHigh = Color(0xFFECEFF4)
val LightSurfaceContainerHighest = Color(0xFFE5E9F0)

// ============================================================================
// MODO OSCURO — Pizarra mineral profunda sin saturación de superficies rojas
// ============================================================================
val DarkPrimary = Color(0xFFFFB3B5)
val DarkOnPrimary = Color(0xFF3B080C)
val DarkPrimaryContainer = Color(0xFF5D1114)
val DarkOnPrimaryContainer = Color(0xFFFFDAD8)

val DarkSecondary = Color(0xFFBEC8D6)
val DarkOnSecondary = Color(0xFF1B2635)
val DarkSecondaryContainer = Color(0xFF303C4C)
val DarkOnSecondaryContainer = Color(0xFFE0E8F3)

val DarkTertiary = Color(0xFFCBD5E1)
val DarkOnTertiary = Color(0xFF1E293B)
val DarkTertiaryContainer = Color(0xFF334155)
val DarkOnTertiaryContainer = Color(0xFFF1F5F9)

val DarkBackground = Color(0xFF0E1117)
val DarkOnBackground = Color(0xFFF3F5F7)
val DarkSurface = Color(0xFF151922)
val DarkOnSurface = Color(0xFFF3F5F7)
val DarkSurfaceVariant = Color(0xFF202633)
val DarkOnSurfaceVariant = Color(0xFFAAB2C0)

val DarkOutline = Color(0xFF7D899D)
val DarkOutlineVariant = Color(0xFF30394A)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

// Contenedores neutros de superficie (Modo Oscuro)
val DarkSurfaceContainerLowest = Color(0xFF0A0D12)
val DarkSurfaceContainerLow = Color(0xFF12161E)
val DarkSurfaceContainer = Color(0xFF181D27)
val DarkSurfaceContainerHigh = Color(0xFF1E2430)
val DarkSurfaceContainerHighest = Color(0xFF252C3A)

// ============================================================================
// ACENTOS SEMÁNTICOS (Texto/Icono sobre superficie y pares de contenedor)
// ============================================================================
val LightSuccess = Color(0xFF146C43)
val LightSuccessContainer = Color(0xFFDCF5E7)
val LightOnSuccessContainer = Color(0xFF0A3B23)

val DarkSuccess = Color(0xFF6DD69B)
val DarkSuccessContainer = Color(0xFF0F3D26)
val DarkOnSuccessContainer = Color(0xFFB8F3D0)

val LightWarning = Color(0xFF8A5300)
val LightWarningContainer = Color(0xFFFFF0D6)
val LightOnWarningContainer = Color(0xFF4A2B00)

val DarkWarning = Color(0xFFF2C166)
val DarkWarningContainer = Color(0xFF4A2D00)
val DarkOnWarningContainer = Color(0xFFFFE0A8)

val LightInfo = Color(0xFF1F5D99)
val LightInfoContainer = Color(0xFFE3F0FF)
val LightOnInfoContainer = Color(0xFF0B2E52)

val DarkInfo = Color(0xFFA9C7FF)
val DarkInfoContainer = Color(0xFF13355B)
val DarkOnInfoContainer = Color(0xFFD6E6FF)

@Immutable
data class PenolesSemanticColors(
    val decorativeAccent: Color,
    val success: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val infoContainer: Color,
    val onInfoContainer: Color
)

val LightSemanticColors = PenolesSemanticColors(
    decorativeAccent = PenolesDecorativeRed,
    success = LightSuccess,
    successContainer = LightSuccessContainer,
    onSuccessContainer = LightOnSuccessContainer,
    warning = LightWarning,
    warningContainer = LightWarningContainer,
    onWarningContainer = LightOnWarningContainer,
    info = LightInfo,
    infoContainer = LightInfoContainer,
    onInfoContainer = LightOnInfoContainer
)

val DarkSemanticColors = PenolesSemanticColors(
    decorativeAccent = PenolesDecorativeRed,
    success = DarkSuccess,
    successContainer = DarkSuccessContainer,
    onSuccessContainer = DarkOnSuccessContainer,
    warning = DarkWarning,
    warningContainer = DarkWarningContainer,
    onWarningContainer = DarkOnWarningContainer,
    info = DarkInfo,
    infoContainer = DarkInfoContainer,
    onInfoContainer = DarkOnInfoContainer
)

val LocalPenolesSemanticColors = staticCompositionLocalOf { LightSemanticColors }

// Alias de compatibilidad hacia atrás para referencias existentes
val PenolesRedDark = LightPrimary
val PenolesRedLight = DarkPrimary
val PenolesRedContainerLight = LightPrimaryContainer
val PenolesOnRedContainerLight = LightOnPrimaryContainer
val PenolesCharcoal = LightOnBackground
val PenolesDarkGray = LightSecondary
val PenolesSlateGray = LightOnSurfaceVariant
val PenolesLightGray = LightBackground
val PenolesSurfaceVariantLight = LightSurfaceVariant
val PenolesWhite = LightSurface
val PenolesSuccessGreen = LightSuccess
val PenolesWarningAmber = LightWarning
val PenolesErrorRed = LightError
val PenolesDarkBackground = DarkBackground
val PenolesDarkSurface = DarkSurface
val PenolesDarkSurfaceVariant = DarkSurfaceVariant
val PenolesDarkOnSurface = DarkOnSurface
val PenolesDarkOnSurfaceVariant = DarkOnSurfaceVariant
