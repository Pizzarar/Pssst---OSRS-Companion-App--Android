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
val OsrsYellow = Color(0xFFFFFF00)         // Stack count < 100K
val OsrsWhite = Color(0xFFFFFFFF)          // Stack count 100K - 9.99M
val OsrsGreen = Color(0xFF00FF80)          // Stack count >= 10M
val OsrsHerbGreen = Color(0xFF4CAF50)      // Grown patch / success green
val OsrsRed = Color(0xFFFF4D4D)            // Warning / error / low approval
val OsrsTextMuted = Color(0xFFB8A88A)      // Muted subtext / parchment
val OsrsTextPrimary = Color(0xFFFFE0A0)    // Primary warm text

// Art Packet Derived Shades — Carved Stone
val OsrsStonePoreDark = Color(0xFF14110D)  // Deep stone pit / mortar line shadow
val OsrsStoneMid = Color(0xFF1E1A15)       // Intermediate stone ground block tone between OsrsDarkStone and OsrsSlotEmpty
val OsrsPanelMid = Color(0xFF27211A)       // Intermediate panel stone mottling tone between OsrsSlotEmpty and OsrsPanelDark
val OsrsStoneFlecks = Color(0xFF473E31)    // Warm mineral fleck highlight in carved stone

// Art Packet Derived Shades — Aged Dark Oak
val OsrsOakBase = Color(0xFF241B13)        // Heartwood plank base fill for top bar & table headers
val OsrsOakMid = Color(0xFF2D2218)         // Intermediate oak plank grain tone between OsrsOakBase and OsrsOakGrainLight
val OsrsOakGrainDark = Color(0xFF16100B)   // Wood growth-ring trough & plank seam shadow
val OsrsOakGrainLight = Color(0xFF36281C)  // Planed oak ridge highlight

// Art Packet Derived Shades — Weathered Parchment (dark-UI vellum)
val OsrsParchmentDark = Color(0xFF332B20)  // Darkened vellum ground (keeps >9:1 contrast with OsrsTextPrimary)
val OsrsParchmentWarm = Color(0xFF3F3527)  // Aged paper highlight stop & linen fiber ridge
val OsrsParchmentStain = Color(0xFF261F16) // Edge foxing & water-stain vignette stop
val OsrsLedgerRowAlt = Color(0xFF312A20)   // Alternating odd row wash in Timers tables (>5.1:1 with OsrsTextMuted)

// Art Packet Derived Shades — Worn Binding Leather
val OsrsLeatherBase = Color(0xFF281C15)    // Oxblood-brown leather cover fill (drawer, pack tray, bookmark tabs)
val OsrsLeatherCrease = Color(0xFF18100C)  // Pebble-grain crease shadow
val OsrsLeatherHighlight = Color(0xFF3A291F) // Worn leather crown highlight
val OsrsLeatherStitch = Color(0xFF7A6548)  // Waxed linen saddle-stitch thread dash

// Art Packet Derived Shades — Wrought Iron & Aged Brass Hardware
val OsrsIronBody = Color(0xFF23201D)       // Forged wrought-iron strap, bracket & hinge fill
val OsrsIronEdgeLight = Color(0xFF524B42)  // Hammered iron bevel glint
val OsrsIronEdgeDark = Color(0xFF0E0C0A)   // Forged iron shadow & deep groove
val OsrsBrassMid = Color(0xFFB8863B)       // Aged brass bezel & plaque trim

// Art Packet Derived Shades — Natural Sprite & Badge Ramps
val OsrsHerbLight = Color(0xFF7CC47F)      // Top-left pixel highlight on natural herb and tree sprites
val OsrsHerbDark = Color(0xFF2E6B31)       // Bottom-right pixel shadow on natural herb and tree sprites
val OsrsWoodLight = Color(0xFF9A6B3A)      // Top-left pixel highlight on wooden sprite hardware (chests, birdhouses, stakes)
val OsrsWoodBase = Color(0xFF6B4826)       // Mid-tone wood fill on natural sprites and empty-state scenes
val OsrsWoodDark = Color(0xFF3F2A16)       // Bottom-right pixel shadow on wooden sprite hardware
val OsrsRedDark = Color(0xFF8E2626)        // Bottom-right pixel shadow on warning badges and danger plaques

// Art Packet Derived Shades — Crimson Sealing Wax
val OsrsWaxCrimson = Color(0xFF7A1C1C)     // Melted crimson wax seal body
val OsrsWaxHighlight = Color(0xFFB83232)   // Wax seal raised rim ridge
val OsrsWaxShadow = Color(0xFF3D0B0B)      // Stamped wax recess & outer shadow

