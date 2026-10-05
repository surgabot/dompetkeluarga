package com.family.financeapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val GreenPrimary = Color(0xFF00875A)
val GreenDark = Color(0xFF005A3C)
val GreenLight = Color(0xFF36B37E)
val TealAccent = Color(0xFF00B8D9)
val GoldYellow = Color(0xFFFFAB00)

val ExpenseRed = Color(0xFFFF5630)
val IncomeGreen = Color(0xFF36B37E)
val CardSurface = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFF7F9FA)

val SuperAppGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0052CC), Color(0xFF00875A))
)

val WalletCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0747A6), Color(0xFF00875A), Color(0xFF00B8D9))
)

val GoldCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFAB00), Color(0xFFFF8B00))
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = TealAccent,
    background = BackgroundLight,
    surface = CardSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    secondary = TealAccent,
    background = Color(0xFF091E42),
    surface = Color(0xFF172B4D)
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
