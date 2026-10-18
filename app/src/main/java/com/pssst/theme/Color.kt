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
val OsrsOuterFrame = Color(0xFF0C0906)     // Outermost 1dp carved-stone frame

// Gold Plaque & Hardware Accents
val OsrsGoldHighlight = Color(0xFFFFCA7A)  // Top/left highlight on gold plaques
val OsrsGoldShadow = Color(0xFF8C4800)     // Bottom/right shadow on gold plaques
val OsrsGoldDark = Color(0xFFD6780E)       // Lower gradient stop on gold plaques
val OsrsRivetLight = Color(0xFF8A7960)     // Corner stud highlight
val OsrsRivetDark = Color(0xFF1F1A14)      // Corner stud shadow

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
