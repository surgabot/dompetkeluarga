package com.family.financeapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GreenPrimary = Color(0xFF0F9D58)
val GreenSecondary = Color(0xFF00796B)
val GreenBackground = Color(0xFFF4FBF7)
val ExpenseRed = Color(0xFFE53935)
val IncomeGreen = Color(0xFF2E7D32)
val CardSurface = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    background = GreenBackground,
    surface = CardSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

@Composable
fun FamilyFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
