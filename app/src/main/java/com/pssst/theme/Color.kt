package com.pssst.theme

import androidx.compose.ui.graphics.Color

// OSRS Classic Interface Color Palette
val OsrsDarkStone = Color(0xFF191612)      // Base background (dark stone)
val OsrsPanelDark = Color(0xFF2B251D)      // Panel background darker gradient stop
val OsrsPanelLight = Color(0xFF3B3327)     // Panel background lighter gradient stop
val OsrsSlotRecessed = Color(0xFF352E24)   // Recessed bank/inventory slot fill
val OsrsSlotEmpty = Color(0xFF221D16)      // Unoccupied slot fill
val OsrsBorderLight = Color(0xFF5D503E)    // Bevel highlight (top/left edge)
val OsrsBorderDark = Color(0xFF120E0A)     // Bevel shadow (bottom/right edge)
val OsrsBorderOutline = Color(0xFF453B2F)  // Neutral border frame

// Typography & Highlight Colors
val OsrsGold = Color(0xFFFF981F)           // Classic OSRS orange/gold title text
val OsrsYellow = Color(0xFFFFFF00)         // Stack count < 100K & highlights
val OsrsWhite = Color(0xFFFFFFFF)          // Stack count 100K - 9.99M
val OsrsGreen = Color(0xFF00FF80)          // Stack count >= 10M
val OsrsHerbGreen = Color(0xFF4CAF50)      // Grown patch / success green
val OsrsRed = Color(0xFFFF4D4D)            // Warning / error / low approval
val OsrsTextMuted = Color(0xFFB8A88A)      // Muted subtext / parchment
val OsrsTextPrimary = Color(0xFFFFE0A0)    // Primary warm text

// Legacy Material aliases
val Purple80 = OsrsGold
val PurpleGrey80 = OsrsTextMuted
val Pink80 = OsrsYellow

val Purple40 = OsrsGold
val PurpleGrey40 = OsrsTextMuted
val Pink40 = OsrsYellow
