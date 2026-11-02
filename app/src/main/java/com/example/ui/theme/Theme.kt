package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = NightPrimary,
    onPrimary = NightOnPrimary,
    primaryContainer = NightPrimaryContainer,
    onPrimaryContainer = NightOnPrimaryContainer,
    secondary = NightSecondary,
    onSecondary = NightOnSecondary,
    secondaryContainer = NightSecondaryContainer,
    onSecondaryContainer = NightOnSecondaryContainer,
    tertiary = NightTertiary,
    onTertiary = NightOnTertiary,
    tertiaryContainer = NightTertiaryContainer,
    onTertiaryContainer = NightOnTertiaryContainer,
    background = NightBackground,
    surface = NightSurface,
    surfaceVariant = NightSurfaceVariant,
    onBackground = NightOnSurface,
    onSurface = NightOnSurface,
    onSurfaceVariant = NightOnSurfaceVariant,
    outline = NightOutline,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BabyPeachPrimary,
    onPrimary = BabyPeachOnPrimary,
    primaryContainer = BabyPeachContainer,
    onPrimaryContainer = BabyPeachOnContainer,
    secondary = SageSecondary,
    onSecondary = SageOnSecondary,
    secondaryContainer = SageContainer,
    onSecondaryContainer = SageOnContainer,
    tertiary = HoneyTertiary,
    onTertiary = HoneyOnTertiary,
    tertiaryContainer = HoneyContainer,
    onTertiaryContainer = HoneyOnContainer,
    background = WarmBackground,
    surface = WarmSurface,
    surfaceVariant = WarmSurfaceVariant,
    onBackground = WarmOnSurface,
    onSurface = WarmOnSurface,
    onSurfaceVariant = WarmOnSurfaceVariant,
    outline = WarmOutline,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set false to prioritize our gentle baby care palette
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
