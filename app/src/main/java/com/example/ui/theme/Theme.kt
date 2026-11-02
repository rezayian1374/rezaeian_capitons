package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = PersianPurplePrimary,
  onPrimary = Color.White,
  primaryContainer = PersianPurpleDark,
  onPrimaryContainer = PersianPurpleSecondary,
  secondary = PersianCyan,
  onSecondary = Color.Black,
  tertiary = PersianGold,
  background = DarkSurface,
  surface = DarkSurfaceCard,
  surfaceVariant = DarkSurfaceCardElevated,
  onBackground = TextPrimaryDark,
  onSurface = TextPrimaryDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = DarkSurfaceBorder
)

private val LightColorScheme = lightColorScheme(
  primary = PersianPurplePrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFEDE9FE),
  onPrimaryContainer = PersianPurpleDark,
  secondary = PersianCyan,
  onSecondary = Color.Black,
  tertiary = PersianGold,
  background = LightSurface,
  surface = LightSurfaceCard,
  surfaceVariant = Color(0xFFF1F5F9),
  onBackground = TextPrimaryLight,
  onSurface = TextPrimaryLight,
  onSurfaceVariant = TextSecondaryLight,
  outline = LightSurfaceBorder
)

@Composable
fun PersianCaptionsTheme(
  darkTheme: Boolean = true, // Default to sleek dark video editing theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = PersianTypography,
    content = content
  )
}
