package com.family.financeapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Palet Warna Asli JetBrains / Kotlin Multiplatform (KMP)
val KmpDarkBg = Color(0xFF0C0E14)          // Latar belakang gelap futuristik KMP
val KmpCardBg = Color(0xFF161926)          // Kartu elevated KMP
val KmpCardBorder = Color(0xFF282C40)      // Border garis halus KMP
val KmpSurfaceAccent = Color(0xFF202436)   // Kontainer elemen dalam kartu

// Gradasi Khas Kotlin (Violet -> Magenta -> Coral/Orange)
val KotlinPurple = Color(0xFF7F52FF)       // Warna primer Kotlin
val KotlinMagenta = Color(0xFFC711E1)      // Warna tengah Kotlin
val KotlinOrange = Color(0xFFE24A00)       // Warna coral Kotlin
val KotlinCyan = Color(0xFF27C4F5)         // Cyan neon KMP
val KotlinGreen = Color(0xFF3CD070)        // Status selesai / tercapai
val KotlinYellow = Color(0xFFFFB300)       // Status in progress

// Text Colors
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFF9DA7C1)
val TextMuted = Color(0xFF6B7280)

// Aliases untuk kompatibilitas layar lain
val ExpenseRed = KotlinOrange
val IncomeGreen = KotlinGreen
val CardSurface = KmpCardBg
val BackgroundLight = KmpDarkBg

// Brushes Gradasi Khas Kotlin Multiplatform
val KotlinGradient = Brush.horizontalGradient(
    colors = listOf(KotlinPurple, KotlinMagenta, KotlinOrange)
)

val KotlinCardGlow = Brush.linearGradient(
    colors = listOf(KotlinPurple.copy(alpha = 0.35f), KotlinMagenta.copy(alpha = 0.15f), Color.Transparent)
)

val KotlinButtonGradient = Brush.horizontalGradient(
    colors = listOf(KotlinPurple, KotlinMagenta)
)

val KmpMilestoneLine = Brush.verticalGradient(
    colors = listOf(KotlinGreen, KotlinPurple, KotlinMagenta, KotlinCyan, TextMuted)
)

private val KmpColorScheme = darkColorScheme(
    primary = KotlinPurple,
    secondary = KotlinCyan,
    tertiary = KotlinOrange,
    background = KmpDarkBg,
    surface = KmpCardBg,
    onPrimary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun FamilyFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Selalu gunakan tema Dark Futuristik gaya Kotlin Multiplatform
    MaterialTheme(
        colorScheme = KmpColorScheme,
        content = content
    )
}
