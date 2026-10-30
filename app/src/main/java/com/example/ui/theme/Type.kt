package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Sistema tipográfico de Peñoles Conecta:
 * - Títulos y encabezados: Montserrat (pesos 600 SemiBold y 700 Bold).
 * - Lectura, navegación, botones y formularios: Plus Jakarta Sans (pesos 400 Regular, 500 Medium y 600 SemiBold).
 *
 * NOTA TÉCNICA DE AUDITORÍA DE FUENTES (Ver ASSET_SOURCES.md):
 * 1. `res/font/montserrat.ttf` fue inspeccionado mediante sus tablas OpenType (`name` y `OS/2`):
 *    es un archivo estático (sin tabla variable `fvar`) cuyo nombre interno es "Montserrat Thin Regular".
 *    Por ello NO se duplica como falso peso Normal/Medium/SemiBold/Bold. Mientras se incorporan los
 *    archivos estáticos `montserrat_semibold.ttf` (600) y `montserrat_bold.ttf` (700) —o una fuente
 *    variable auténtica con eje `wght`—, `MontserratDisplayFamily` utiliza temporalmente
 *    `FontFamily.SansSerif` del sistema para garantizar pesos 600 y 700 reales y legibles.
 * 2. `res/font/plus_jakarta_sans.ttf` es un archivo estático (`has_fvar = false`) correspondiente a
 *    "Plus Jakarta Sans Regular" (`usWeightClass = 400`). Se registra únicamente para `FontWeight.Normal`
 *    sin falsificar los pesos 500 y 600 con el mismo binario estático.
 */
val MontserratFamily: FontFamily = FontFamily.SansSerif

val PlusJakartaSansRegularFamily: FontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal)
)

/**
 * Para estilos 500 (Medium) y 600 (SemiBold) de Plus Jakarta Sans, mientras no existan los archivos
 * `plus_jakarta_sans_medium.ttf` y `plus_jakarta_sans_semibold.ttf` en `res/font/`, se utiliza
 * `FontFamily.SansSerif` para preservar la diferenciación real de peso tipográfico sin inventar
 * pesos sobre el archivo estático 400.
 */
val PlusJakartaSansWeightedFamily: FontFamily = FontFamily.SansSerif

val Typography = Typography(
    // Escala superior armonizada para que ningún componente caiga en otra familia por accidente
    displayLarge = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    displayMedium = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),
    displaySmall = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    // Título principal: Montserrat 700, 28/34 sp
    headlineLarge = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    // Encabezado: Montserrat 600, 24/30 sp
    headlineMedium = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    // Sección: Montserrat 600, 20/26 sp
    titleLarge = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    // Tarjeta: Plus Jakarta Sans 600, 16/22 sp
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSansWeightedFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PlusJakartaSansWeightedFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Lectura: Plus Jakarta Sans 400, 16/24 sp
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSansRegularFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // Información secundaria: Plus Jakarta Sans 400, 14/20 sp
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSansRegularFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Etiquetas / texto secundario compacto: Plus Jakarta Sans 400, 12/16 sp (nunca < 12.sp)
    bodySmall = TextStyle(
        fontFamily = PlusJakartaSansRegularFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    // Botones: Plus Jakarta Sans 600, 14/20 sp
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSansWeightedFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Etiquetas: Plus Jakarta Sans 500, 12/16 sp
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSansWeightedFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    // Mínimo funcional estricto de 12/16 sp para evitar textos ilegibles de 10-11 sp
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSansWeightedFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
