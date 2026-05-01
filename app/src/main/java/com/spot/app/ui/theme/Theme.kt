package com.spot.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════
//  SPOT  —  iOS GLASS DESIGN SYSTEM
// ═══════════════════════════════════════════════

// ── Base backgrounds ──────────────────────────
val SpotBlack        = Color(0xFF050508)   // true near-black
val SpotDeep         = Color(0xFF0D0D12)   // page background
val SpotSurface      = Color(0xFF13131A)   // card base

// ── Glass layers (white at very low opacity) ──
// These simulate frosted glass on dark backgrounds
val GlassWhite5      = Color(0x0DFFFFFF)   // 5% white  – subtle tint
val GlassWhite10     = Color(0x1AFFFFFF)   // 10% white – card fill
val GlassWhite15     = Color(0x26FFFFFF)   // 15% white – elevated card
val GlassWhite25     = Color(0x40FFFFFF)   // 25% white – border/stroke
val GlassWhite40     = Color(0x66FFFFFF)   // 40% white – active border

// ── Text ──────────────────────────────────────
val SpotWhite        = Color(0xFFFFFFFF)
val SpotWhite80      = Color(0xCCFFFFFF)   // 80% white – primary text
val SpotWhite50      = Color(0x80FFFFFF)   // 50% white – secondary
val SpotWhite30      = Color(0x4DFFFFFF)   // 30% white – hints

// ── Accent — warm gold/amber (NOT blue/purple) ─
val SpotGold         = Color(0xFF3F51B5)   // muted gold
val SpotGoldDim      = Color(0x33D4AF6A)   // gold at 20% – glow bg
val SpotGoldBright   = Color(0xFFF0CC88)   // brighter gold – highlights

// ── Seat status ───────────────────────────────
val SeatFree         = Color(0xFF4CD964)   // iOS green
val SeatTaken        = Color(0xFFFF453A)   // iOS red
val SeatHeld         = Color(0xFFFFD60A)   // iOS yellow
val SeatPicked       = Color(0xFFD4AF6A)   // gold = selected

// ── Utility ───────────────────────────────────
val SpotDivider      = Color(0x1AFFFFFF)   // 10% white line
val SpotSuccess      = Color(0xFF4CD964)
val SpotWarn         = Color(0xFFFFD60A)
val SpotDanger       = Color(0xFFFF453A)

// ── Typography ────────────────────────────────
val SpotTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = SpotWhite80,
        letterSpacing = (-1.2).sp
    ),
    headlineMedium = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        color = SpotWhite80,
        letterSpacing = (-0.8).sp
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = SpotWhite80,
        letterSpacing = (-0.5).sp
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = SpotWhite80,
        letterSpacing = (-0.2).sp
    ),
    bodyLarge = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        color = SpotWhite80,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        color = SpotWhite50,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        color = SpotWhite30,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = SpotWhite30,
        letterSpacing = 1.2.sp
    )
)

// ── App theme wrapper ─────────────────────────
@Composable
fun SpotTheme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        background     = SpotDeep,
        surface        = SpotSurface,
        primary        = SpotGold,
        onPrimary      = SpotBlack,
        onBackground   = SpotWhite80,
        onSurface      = SpotWhite80,
        outline        = GlassWhite25
    )
    MaterialTheme(
        colorScheme = colors,
        typography  = SpotTypography,
        content     = content
    )
}