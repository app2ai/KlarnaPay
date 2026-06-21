package com.rtech.klarnapay.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Klarna brand colours ──────────────────────────────────────────────────────
private val KlarnaSignaturePink  = Color(0xFFFFB3C7)
private val KlarnaDeepPink       = Color(0xFFE8004A)
private val KlarnaSurface        = Color(0xFFFAFAFA)
private val KlarnaOnSurface      = Color(0xFF1A1A1A)
private val KlarnaOutline        = Color(0xFFD1D1D1)

private val LightColors = lightColorScheme(
    primary            = KlarnaDeepPink,
    onPrimary          = Color.White,
    primaryContainer   = KlarnaSignaturePink.copy(alpha = 0.3f),
    onPrimaryContainer = KlarnaDeepPink,
    secondary          = Color(0xFF4A4A68),
    surface            = KlarnaSurface,
    onSurface          = KlarnaOnSurface,
    outline            = KlarnaOutline
)

/**
 * App-level Material3 theme with Klarna branding.
 */
@Composable
fun KlarnaPayTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        content     = content
    )
}