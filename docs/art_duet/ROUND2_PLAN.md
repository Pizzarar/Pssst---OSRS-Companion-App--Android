# Round 2 Art Duet Plan — OSRS Login-Screen Stone Masonry & Miscellania Castle

## 1. Diagnosis of Owner Emulator Feedback
> *"i dont like hte kindom art, would rather it just be a castle or something. would really like them to try fixing the backgorund and bar background art its so ugly. Theres for sure a way to make it more ruenscapey with like the login screenstone or something."*

Visual inspection of `01_setup.png`, `02_overview.png`, `17_overview_vignettes.png`, and `frames_plaques_ornaments.png` confirms three root causes:
1. **Kingdom Art (`KINGDOM_HEALTHY` / `KINGDOM_WARNING`)**: Currently depicts Advisor Ghrim's bust next to a small wooden box. At 32×24 on a phone card (`17_overview_vignettes.png`), a bearded head + box feels cramped and generic rather than evoking the Kingdom of Miscellania. Replacing both states with an **OSRS Miscellania Castle** silhouette (twin crenellated stone turrets, central keep, arched portcullis gatehouse, wall torches, and royal banner) gives an immediate, iconic OSRS silhouette.
2. **App Background (`Textures.drawStoneGround`)**: Currently draws tiny 6–10px courses of 10–22px bricks where every single brick has an unbroken 1px light border (`OsrsPanelDark` `#2B251D`) and noisy per-pixel interior dithering. On full-phone screens (`01_setup.png`, `02_overview.png`), this creates a hyper-busy, repetitive chocolate-bar grid behind the UI cards instead of the iconic **OSRS login-screen carved dungeon stone** (large, heavy ashlar stone blocks with rounded/chamfered mortar corners, broken chiseled top-edge catch-lights, radial torchlit vignette from upper-center, and subtle stone cracks/pores).
3. **Top Bar (`Frames.drawOakBeamFrame`), Bottom Bar (`Frames.drawStoneSillFrame`), and Title Plaque (`Plaques.drawPlaqueFrame`)**:
   - `drawOakBeam` produces streaky, blotchy brown horizontal lines with dark oval "knots" that read on the emulator as muddy streaks across the Top Bar (`02_overview.png`) and the "Welcome to Pssst" Title Plaque (`01_setup.png`).
   - `drawStoneSillFrame` is a flat mottled rectangle with a single thin yellow line, lacking the chiseled stone block joints and tablet button framing of OSRS login-screen UI chrome.

---

## 2. Specification of Changes

### A. `tools/art/vignettes.py` -> `SceneArt.kt` (`KINGDOM_HEALTHY` & `KINGDOM_WARNING`, 32×24)
Replace `kingdom(warning: bool)` with a 32×24 **Miscellania Castle** scene sharing one architectural layout across both states:
- **Left & Right Flanking Turrets (`x = 2..9` and `x = 22..29`, `y = 6..22`)**:
  - 3 crenellated merlons across the top of each turret (`y = 6..7`), 1px overhang cornice at `y = 8`, vertical arrow slits (`x = 5, y = 11..13` and `x = 25, y = 11..13` in `Outline`), and horizontal mortar courses at `y = 12, 16, 20`.
- **Central Keep & Battlements (`x = 9..22, y = 8..22`)**:
  - Crenellated parapet at `y = 8..9`, stone ashlar wall in `StoneLight` (dominant, contrast 3.23:1 vs panel stone) and `StoneMid` (contrast 1.98:1 vs panel stone) with `StoneDark` mortar details so >75% of non-outline pixels exceed the 1.8:1 contrast floor in `SceneGridTest`.
- **Gatehouse & Portcullis (`x = 12..19, y = 13..22`)**:
  - Stepped stone arch (`x = 12..19, y = 13..22`) framing a recessed gate opening (`x = 13..18, y = 14..22`).
  - Iron portcullis grate (`IronLight` / `StoneLight` vertical bars at `x = 14, 16, 17` and horizontal crossbar at `y = 16, 19`) over `HollowDark` (`#1A140E`).
- **State Differentiation**:
  - **`KINGDOM_HEALTHY` (`warning = False`)**:
    - Crisp `StoneLight` / `StoneMid` masonry.
    - Tall flagstaff (`x = 15, y = 0..7`) flying a proud **royal gold-and-emerald banner** (`x = 16..24, y = 1..5`) with `GoldBright` top/bottom trim, `HerbLight` / `GemEmerald` field, and a `GoldBright` crown/crest dot in the center.
    - Two warm lit wall torches flanking the gatehouse (`x = 10..11` and `x = 20..21`, `y = 13..16`) with `FlameOrange` + `GoldBright` flames.
  - **`KINGDOM_WARNING` (`warning = True`)**:
    - Weathered masonry with a jagged dark structural crack (`Outline` / `StoneDark`) running down the right turret (`x = 25..27, y = 8..16`) and missing a merlon tooth.
    - Bent/tilted flagstaff with a **tattered drooping crimson warning banner** (`EyeCrimson` / `CrownVelvet`, `x = 16..21, y = 2..7`, frayed bottom edge) and a red warning exclamation/flare.
    - Gatehouse portcullis and flanking braziers glowing with urgent crimson/amber alarm light (`EyeCrimson`, `FlameOrange`, `GoldBright` sparks).

### B. `Textures.kt` — OSRS Login-Screen Carved Stone Masonry (`drawStoneGround`, `drawPanelStone`, `drawOakBeam`)
All existing tone arrays (`STONE_GROUND_TONES`, `PANEL_STONE_TONES`, `OAK_TONES`, etc.) remain strictly within their declared `Color.kt` tokens so `MaterialToneTest` and `ArtContrastTest` pass 100%:
1. **`drawStoneGround(canvas, seed)` — OSRS Login-Screen Ashlar Dungeon Wall**:
   - **Monumental Block Proportions**: Increase course height to `11..16` art-px (`11 + hash % 6`) and block width to `20..36` art-px (`20 + hash % 17`) with staggered running-bond offsets per row (non-repeating across 32×32).
   - **Chamfered Ashlar Mortar Joints**:
     - 1px dark mortar (`STONE_GROUND_TONES[0]`, `#14110D`) along bottom (`py == mortarY`) and right (`px == xEnd - 1`), PLUS 1px diagonal corner chamfers at all 4 corners of each block (`(py == y || py == mortarY - 1) && (px == x || px == xEnd - 2)` set to `STONE_GROUND_TONES[0]` or `STONE_GROUND_TONES[1]`). This rounds off the brick corners exactly like OSRS login-screen stone blocks!
   - **Weathered Chiseled Bevels & Radial Torchlight Vignette**:
     - Compute a smooth integer radial torchlight factor centered at `(w / 2, h / 4)` so blocks near the upper-center brazier zone bias toward warmer/lighter stone tones (`STONE_GROUND_TONES[2..4]`) while blocks toward the outer edges/bottom settle into deeper dungeon stone (`STONE_GROUND_TONES[1..3]`).
     - Top/left 1px chiseled catch-light (`py == y && px in (x+2)..(xEnd-4)` or `px == x && py in (y+2)..(mortarY-3)`) is selectively broken by weathered gaps (~30% of edge pixels step down one tone) so it looks like hand-carved ancient stone rather than a bright wireframe grid.
     - Interior block surface uses larger, calmer low-frequency stone slab mottling (`scale = 8`) with occasional 2–3px diagonal hairline stone fissures (`STONE_GROUND_TONES[0]`/`[1]`) instead of noisy high-frequency per-pixel jitter.
2. **`drawPanelStone(canvas, seed)` — Subtle Chiseled Tablet Interior**:
   - Calmer, broader stone slab grain (`scale = 9` coarse + subtle horizontal chisel marks) strictly within `PANEL_STONE_TONES` (`#221D16`, `#27211A`, `#2B251D`) so card interiors read as smooth carved stone tablets with all 3 tones represented.
3. **`drawOakBeam(canvas, seed, ...)` — Clean Planed Timber**:
   - Remove the blotchy oval knots that looked like dirt stains; use clean horizontal planed grain and crisp bevelled plank edges within `OAK_TONES` (and `IRON_TONES` when `showEndBands = true`) so `MaterialToneTest.oakBeam_usesOnlyDeclaredOakAndIronTones` passes cleanly.

### C. `Frames.kt`, `Plaques.kt`, and `DashboardScreen.kt` — OSRS Login-Screen Stone Bar & Plaque Chrome
1. **`Frames.drawOakBeamFrame(canvas, seed)` (Top Status Bar Background)**:
   - Overhaul from a muddy wooden plank into a **Heavy OSRS Login-Screen Carved Stone Header Architrave**:
     - Base fill: Chiseled ashlar stone header blocks (`PANEL_STONE_TONES` + `STONE_GROUND_TONES` recessed vertical block joints every ~32 art-px with chamfered corners and 1px `OsrsBorderLight` top/left block catch-lights).
     - Top & bottom molded stone cornice rails:
       - Outer 1px `OsrsOuterFrame` (`#0C0906`).
       - Top 2px forged iron & brass architrave molding (`OsrsIronEdgeLight` `#524B42` + `OsrsIronBody` `#23201D` with an inset 1px `OsrsBrassMid` `#B8863B` chiseled fillet at `y = 3`).
       - Bottom 3px heavy stone-and-iron sill (`y = h - 4`: `OsrsBrassMid` highlight fillet, `y = h - 3`: `OsrsIronBody`, `y = h - 2`: `OsrsBorderDark` `#120E0A`, `y = h - 1`: `OsrsOuterFrame`).
     - Corner hardware: Forged wrought-iron corner straps with seated rivets (`drawPixelHingeStraps`).
2. **`Frames.drawStoneSillFrame(canvas, seed)` (Bottom Navigation Bar Background) & `DashboardScreen.kt`**:
   - Overhaul `drawStoneSillFrame` into a **Heavy OSRS Login-Screen Carved Stone Footer Pedestal**:
     - Carved ashlar stone block courses with vertical masonry joints aligned with the 5 navigation columns (`w / 5`), chamfered block corners, a top 3px sculpted stone-and-brass cornice rail (`y = 0`: `OsrsOuterFrame`, `y = 1`: `OsrsBrassMid`, `y = 2`: `OsrsIronBody`, `y = 3`: `OsrsBorderDark`), bottom chiseled base shadow, and corner iron brackets (`drawPixelCornerLBrackets`).
   - In `DashboardScreen.kt` bottom bar, give unselected tabs a subtle recessed stone button slot (`Modifier.padding(horizontal = 3.dp, vertical = 4.dp)`) and selected tabs the highlighted recessed/raised OSRS login-screen stone tablet look.
3. **`Plaques.drawPlaqueFrame(canvas, seed)` (Title Plaque on SetupScreen & Drawer)**:
   - Replace `Textures.drawOakBeam(...)` inside `drawPlaqueFrame` with carved OSRS login-screen monument stone (`Textures.drawPanelStone` + ashlar block joint lines near the ends + double chiseled border: outer `OsrsOuterFrame`, 1px `OsrsBrassMid` / `OsrsBorderDark` outer bevel, 1px `OsrsSlotEmpty` recessed channel, inner 1px `OsrsBorderLight` / `OsrsBorderDark` stone tablet bevel, 2-step `stairCorner`s, and flanking 3×3 brass/iron rivets).
