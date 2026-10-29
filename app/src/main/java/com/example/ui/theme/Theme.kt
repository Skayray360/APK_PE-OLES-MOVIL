package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = PenolesRed,
    onPrimary = PenolesWhite,
    primaryContainer = PenolesRedContainerLight,
    onPrimaryContainer = PenolesRedDark,
    secondary = PenolesSilver,
    onSecondary = PenolesWhite,
    secondaryContainer = PenolesBorderGray,
    onSecondaryContainer = PenolesBlack,
    tertiary = PenolesGoldAccent,
    onTertiary = PenolesWhite,
    background = PenolesBackgroundGray,
    onBackground = PenolesBlack,
    surface = PenolesWhite,
    onSurface = PenolesBlack,
    surfaceVariant = PenolesBackgroundGray,
    onSurfaceVariant = PenolesTextGray,
    outline = PenolesBorderGray,
    outlineVariant = PenolesBorderGray
)

private val DarkColorScheme = darkColorScheme(
    primary = PenolesRed,
    onPrimary = PenolesWhite,
    primaryContainer = PenolesRedContainerDark,
    onPrimaryContainer = PenolesRedContainerLight,
    secondary = PenolesDarkTextSecondary,
    onSecondary = PenolesBlack,
    secondaryContainer = PenolesDarkSurfaceVariant,
    onSecondaryContainer = PenolesWhite,
    tertiary = PenolesGoldAccent,
    onTertiary = PenolesWhite,
    background = PenolesDarkBackground,
    onBackground = PenolesWhite,
    surface = PenolesDarkSurface,
    onSurface = PenolesWhite,
    surfaceVariant = PenolesDarkSurfaceVariant,
    onSurfaceVariant = PenolesDarkTextSecondary,
    outline = PenolesDarkBorder,
    outlineVariant = PenolesDarkBorder
)

val PenolesShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun PenolesConectaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = PenolesShapes,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    PenolesConectaTheme(darkTheme = darkTheme, content = content)
}
