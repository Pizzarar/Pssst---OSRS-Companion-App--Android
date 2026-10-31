# Pssst — OSRS World-Flavor Pixel Art Overhaul Brief (`docs/ART_DUET_BRIEF.md`)

## 1. Owner Direction & Goal
Upgrade Pssst's Android app pixel art from stiff procedural/geometric boxes to rich, expressive OSRS world-flavor sprites and scenes using the **Piskel MCP (`C:\Dev\Tools\piskel-mcp-server`)** and the **6-Step Gemini + Claude Art Duet** workflow.
- Note from Owner: The RuneLite plugin needs nothing visually (it only sends data in the background); all art work is in the Android app.

## 2. Workflow: The 6-Step Gemini + Claude Art Duet
Every art batch runs as a Gemini + Claude duet orchestrated by `★ LEAD · PSSST ART`:
1. **Step 1 — PLAN (Gemini `antigravity-cli/gemini-4-argon`)**:
   - Inspects existing `com.pssst.theme.art` files and `build/art-sheet/*.png` (generated via `./gradlew testDebugUnitTest --tests com.pssst.theme.art.ArtSheetExportTest`).
   - Writes `docs/art_duet/PSSST_ART_PLAN.md` + `docs/art_duet/duet_log.md` defining exact pixel dimensions, OSRS material palettes, silhouettes, and composition for every asset below.
2. **Step 2 — CRITIQUE (Claude Opus `antigravity-cli/claude-opus-5-5` or `claude/claude-opus-5-5`)**:
   - Reviews the plan against the codebase (`PixelCanvas.kt`, `PixelArt.kt`, `Icons.kt`, `EmptyStates.kt`, `Ornaments.kt`, `OverviewScreen.kt`, `ic_launcher_foreground.xml`) and existing guardrail tests (`ArtContrastTest`, `ColourDisciplineTest`, `MaterialToneTest`, `PixelHonestyTest`, `SpriteGridTest`).
   - Writes `docs/art_duet/PSSST_ART_CRITIQUE.md` (`BLOCKING / SHOULD / NIT`).
3. **Step 3 — REVISE (Gemini)**:
   - Revises `PSSST_ART_PLAN.md` to resolve all critique items.
4. **Step 4 — BUILD (Claude)**:
   - Uses Piskel MCP (`C:\Dev\Tools\piskel-mcp-server`) / pixel sprite tooling to author the new sprites and scenes, integrates them into `com.pssst.theme.art.*` and launcher drawables, and runs `ArtSheetExportTest` + `ScreenCaptureExportTest` to export scaled PNG sheets (`build/art-sheet/sprites.png`, `scenes.png`, `materials.png`, `frames_plaques_ornaments.png`, and screen captures).
5. **Step 5 — SHOT REVIEW (Gemini)**:
   - Reads the exported PNG contact sheets and screen captures, critiquing silhouette readability, OSRS authenticity, shading, and contrast at real phone density. Writes `docs/art_duet/PSSST_SHOT_REVIEW.md`.
6. **Step 6 — FIX & PR (Claude)**:
   - Applies visual fixes from Step 5, re-exports contact sheets, verifies `./gradlew testDebugUnitTest` passes 100%, and opens a PR.

## 3. Target Asset Roster

| Asset / Surface | Code Location | Upgrade Specification |
| :--- | :--- | :--- |
| **App Icon & Setup Banner** | `app/src/main/res/drawable/ic_launcher_foreground.xml`, `app/src/main/java/com/pssst/ui/setup/SetupScreen.kt` | Replace flat vector `'P'` plaque with a pixel-art OSRS informant crest: shady hooded OSRS figure peeking from behind a castle stone pillar, hand cupped whispering *"Pssst..."* with a glowing gold sync scroll. |
| **`CLOSED_CHEST` (Bank Empty)** | `app/src/main/java/com/pssst/theme/art/EmptyStates.kt` | Snoozing OSRS Bank Clerk leaning on the teller grate next to an open, cobwebbed bank chest with a single moth flying out. |
| **`FALLOW_PATCH` (No Active Crops)** | `app/src/main/java/com/pssst/theme/art/EmptyStates.kt` | Grumpy Tool Leprechaun sitting on an upturned Ultracompost bucket beside a rake stuck in furrowed dirt and a crow eyeing weeds. |
| **`EMPTY_BIRDHOUSE` (No Birdhouses)** | `app/src/main/java/com/pssst/theme/art/EmptyStates.kt` | Fossil Island clockwork bird perched upside-down on a redwood birdhouse roof, peering into the dark entrance hole with a `?`. |
| **`POTION_RACK` (Potion Storage Empty)** | `app/src/main/java/com/pssst/theme/art/EmptyStates.kt` | Oak alchemy shelf with a tipped-over empty vial, a green herblore drip hanging off the plank, and a pestle & mortar. |
| **`QUEST_SCROLL` (No Matching Quests)** | `app/src/main/java/com/pssst/theme/art/EmptyStates.kt` | Rolled parchment pinned to a tavern oak post with a Rune dagger and the iconic blue OSRS Quest Star wax seal. |
| **`SUNDIAL` (Dailies Complete / Empty)** | `app/src/main/java/com/pssst/theme/art/EmptyStates.kt` | Zaff's empty battlestaff barrel + open NMZ herb box with a `"BACK AT RESET"` wooden sign hung on a carved stone sundial. |
| **`LEDGER_BOOKMARK` (No GP Goal Set)** | `app/src/main/java/com/pssst/theme/art/EmptyStates.kt` | Velvet display pedestal with a dotted chalk outline of a Twisted Bow / GP stack and a brass price tag reading `? GP`. |
| **16×16 Nav, Chip & Status Sprites** | `app/src/main/java/com/pssst/theme/art/Icons.kt`, `Ornaments.kt` | Refine all 16 `ArtIcons` (`OverviewShield`, `StorageChest`, `ExchangeCoins`, `ProgressStar`, `TimersHourglass`, `DrawerLedgerMenu`, `EditQuill`, `Gear`, `Check`, `Birdhouse`, `HerbSprig`, `TreeCrown`, `BadgeReady` bursting Ranarr leaf, `BadgeGrowing` tipping watering can over sprout, `BadgeEmpty`, `BadgeWarning` wilted diseased leaf with buzzing flies) + `WatermarkScaleSprite`. If 6-tone ASCII (`o/h/b/s/a/d`) is too restrictive for organic shading, extend `Sprite` to support richer Piskel-exported palettes while preserving `ArtContrastTest` and `PixelHonestyTest`. |
| **Overview Kingdom & Slayer Vignettes** | `app/src/main/java/com/pssst/ui/main/OverviewScreen.kt` | **Kingdom**: Overflowing royal coffer when healthy vs. sweating Advisor Ghrim / cracked crown when approval <90% or coffer <750k GP. **Slayer**: Sword-through-skull Slayer emblem (glowing gold when task hits 0). |

## 4. Keep Unchanged
- Procedural background stone/wood/leather/parchment shaders in `Textures.kt`, `Frames.kt`, and `Plaques.kt`.
- Bundled OSRS item icons and integer-multiple `PixelItemImage` rendering.
