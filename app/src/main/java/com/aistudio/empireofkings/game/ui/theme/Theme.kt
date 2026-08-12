package com.aistudio.empireofkings.game.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EmpireColorScheme =
  darkColorScheme(
    primary = GoldPrimary,
    secondary = MysticPurple,
    tertiary = CyanMagic,
    background = ImperialDarkBg,
    surface = ObsidianSurface,
    onPrimary = ImperialDarkBg,
    onSecondary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight
  )

@Composable
fun EmpireOfKingsTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = EmpireColorScheme, typography = Typography, content = content)
}
