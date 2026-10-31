# Pssst Pixel-Art Overhaul Plan — Revised (`PSSST_ART_PLAN.md`)

**Author:** Gemini (`antigravity-cli/gemini-4-argon`) — Step 1 PLAN & Step 3 REVISE (incorporating Claude Opus Step 2 Critique)
**Branch:** `feat/piskel-art-duet`
**Target:** Replace stiff geometric placeholders with expressive OSRS world-flavor pixel art across the launcher icon, Setup screen crest, 7 empty-state scenes, 16 `ArtIcons` + `WatermarkScaleSprite`, and Overview Kingdom/Slayer vignettes.

---

## 1. Architectural & Guardrail Invariants

Every asset and test in this overhaul must pass `./gradlew testDebugUnitTest`:

1. **`ColourDisciplineTest` (`app/src/test/java/com/pssst/theme/ColourDisciplineTest.kt`)**:
   - Scans raw source text of every `.kt` file (including comments/KDoc) outside `Color.kt`, `BankScreen.kt`, and `InventoryScreen.kt` for substrings `"OsrsGreen"`, `"OsrsYellow"`, `"OsrsWhite"`, `"0xFF00FF80"`, `"0xFFFFFF00"`.
   - **Rule (resolves S4)**: Never use identifier names or KDoc comments containing `"OsrsGreen"`, `"OsrsYellow"`, or `"OsrsWhite"` (e.g., use `ClerkCollarBone`, `LeprechaunHatMid`, `SandGoldLight`). Never use `0xFFFFFFFF` (`OsrsWhite`), `0xFF00FF80`, or `0xFFFFFF00` in any sprite or scene palette.
   - Use `OsrsOuterFrame.toArgb()` (`0xFF0C0906.toInt()`) for all `'o'` 1px outlines.
2. **`PixelHonestyTest` (`app/src/test/java/com/pssst/theme/art/PixelHonestyTest.kt`)**:
   - Scans raw source text of `app/src/main/java/com/pssst/theme/**` for forbidden tokens (`Brush.verticalGradient`, `Brush.horizontalGradient`, `Brush.linearGradient`, `Brush.radialGradient`, `drawCircle`, `drawOval`, `drawRoundRect`, `RoundedCornerShape`, `StrokeCap.Round`, `StrokeJoin.Round`, `dashPathEffect`, `ShaderBrush`, `WaxSealMedallion`, `OsrsWaxCrimson`, `OsrsWaxHighlight`, `OsrsWaxShadow`, `OsrsLedgerRowAlt`, `OsrsStoneFlecks`).
   - **Rule**: Never name any helper or comment with those substrings. All drawing uses integer `PixelCanvas` operations (`set`, `fill`, `hLine`, `vLine`, `rectOutline`, `bevelBox`, `blit`, `drawSceneGrid`).
3. **`SpriteGridTest` (`app/src/test/java/com/pssst/theme/art/SpriteGridTest.kt`)**:
   - `ArtIcons.ALL_SPRITES` has exact size `16`, each `16×16`.
   - Every non-outline fill pixel (`h`, `b`, `s`, `a`, `d`, `l`, `m`, `k`, `g`) must be 4-neighbour enclosed by `'o'` or other non-transparent pixels—never 4-adjacent to `'.'` and never on row/col `0` or `15`.
   - Extend `SpriteGridTest` to also validate `Ornaments.WatermarkScaleSprite` (`16×16`) and `Ornaments.BulletDiamondSprite` (`7×7`), and add a new assertion that every `16×16` sprite has `>= 20` transparent `'.'` pixels and `>= 12` outline `'o'` pixels (**resolves S8, S9**).
4. **`ArtContrastTest` & `MaterialToneTest`**:
   - Keep `Textures.kt`, `Frames.kt`, `Plaques.kt`, and `Tables.kt` procedural material shaders and tone ramps untouched.
5. **`GuardrailsTest`**:
   - Never include `"runescape.wiki"`, `"oldschool.runescape"`, or `"static.runelite.net"` in any `.kt`, `.java`, or `.xml` file or comment under `src/main` (**resolves N7**).

---

## 2. `SpriteTones`, `SceneGrid` & `ScenePalettes` (`PixelCanvas.kt` + `ScenePalettes.kt`)

### 2A. Non-Colliding `SpriteTones` Extension (`PixelCanvas.kt` — resolves B1)
Preserve the existing 6 role characters in `PixelCanvas.kt` (`o` = outline, `h` = highlight, `b` = base body, `s` = shadow, `a` = accent, `d` = accentDark, `.` = transparent `0`) with zero breaking changes. Extend `SpriteTones` with 4 optional nullable `Int?` secondary material roles that automatically fall back to the primary ramp when `null` (so `GOLD` and `STONE` retints never produce transparent holes):

```kotlin
data class SpriteTones(
    val outline: Int = OsrsOuterFrame.toArgb(),
    val highlight: Int,
    val base: Int,
    val shadow: Int,
    val accent: Int = OsrsIronEdgeLight.toArgb(),
    val accentDark: Int = OsrsIronBody.toArgb(),
    val secondaryLight: Int? = null,
    val secondaryMid: Int? = null,
    val secondaryDark: Int? = null,
    val glint: Int? = null
) {
    fun colorFor(role: Char): Int = when (role) {
        'o' -> outline
        'h' -> highlight
        'b' -> base
        's' -> shadow
        'a' -> accent
        'd' -> accentDark
        'l' -> secondaryLight ?: highlight
        'm' -> secondaryMid ?: base
        'k' -> secondaryDark ?: shadow
        'g' -> glint ?: highlight
        '.', ' ' -> 0
        else -> throw IllegalArgumentException("Unknown sprite role character: '$role'")
    }
```

Add new named presets to `SpriteTones.Companion` (and register all of them in `SpriteGridTest.spriteToneSets_neverUseStackCountColours`):
- `QUEST_BLUE`: `highlight = 0xFF68A2F2.toInt()`, `base = 0xFF2E6BC6.toInt()`, `shadow = 0xFF193E7A.toInt()`, `accent = OsrsGoldHighlight.toArgb()`, `accentDark = OsrsGold.toArgb()`, `glint = OsrsTextPrimary.toArgb()`
- `TERRACOTTA_SPROUT`: `highlight = OsrsHerbLight.toArgb()`, `base = OsrsHerbGreen.toArgb()`, `shadow = OsrsHerbDark.toArgb()`, `accent = OsrsGoldHighlight.toArgb()`, `accentDark = OsrsBrassMid.toArgb()`, `secondaryLight = 0xFF78D4E4.toInt()`, `secondaryMid = 0xFF48A8B8.toInt()`, `secondaryDark = 0xFF286470.toInt()`, `glint = OsrsTextPrimary.toArgb()`
- `BLIGHT_WARNING`: `highlight = OsrsRed.toArgb()`, `base = 0xFFB85D28.toInt()`, `shadow = OsrsRedDark.toArgb()`, `accent = OsrsTextPrimary.toArgb()`, `accentDark = OsrsGold.toArgb()`, `secondaryLight = 0xFFD8C398.toInt()`, `secondaryMid = 0xFF8C7A5B.toInt()`, `secondaryDark = 0xFF4E4332.toInt()`
- Enhance existing `HERB`, `WOOD`, `GOLD`, `LEATHER`, `PARCHMENT`, `STONE` with optional `secondaryLight`/`secondaryMid`/`secondaryDark`/`glint` where helpful while keeping primary ramps unchanged.

### 2B. `SceneGrid` & `drawSceneGrid` (`PixelCanvas.kt` — resolves B7, S7)
Add `SceneGrid` and `PixelCanvas.drawSceneGrid` in `PixelCanvas.kt` using `Int` ARGB values (no Compose `Color` dependency):

```kotlin
class SceneGrid(
    val rows: List<String>,
    val palette: Map<Char, Int>
) {
    val height: Int = rows.size
    val width: Int = rows.firstOrNull()?.length ?: 0

    init {
        require(rows.isNotEmpty() && width > 0) { "SceneGrid must not be empty" }
        rows.forEachIndexed { idx, r ->
            require(r.length == width) { "SceneGrid row $idx length ${r.length} != $width" }
            for (ch in r) {
                require(ch == '.' || ch == ' ' || palette.containsKey(ch)) {
                    "Unmapped SceneGrid char '$ch' at row $idx"
                }
            }
        }
    }
}

fun PixelCanvas.drawSceneGrid(grid: SceneGrid, ox: Int = 0, oy: Int = 0) {
    for (y in 0 until grid.height) {
        val row = grid.rows[y]
        for (x in 0 until grid.width) {
            val ch = row[x]
            if (ch == '.' || ch == ' ') continue
            val argb = grid.palette[ch] ?: throw IllegalArgumentException("Unknown scene char '$ch'")
            if (argb != 0) set(ox + x, oy + y, argb)
        }
    }
}
```

### 2C. Centralised `ScenePalettes.kt` & Contrast Floor (resolves S2, S4, S5)
Create `app/src/main/java/com/pssst/theme/art/ScenePalettes.kt` holding named `Int` ARGB constants shared across all scenes, vignettes, and launcher icon verification.
- **Background Contrast Rule (resolves S2)**: `STONE_GROUND_TONES` and `PANEL_STONE_TONES` sit around `#221D16`–`#2B251D`. Dark silhouettes (informant hood, crow, hollow chest interior, dark birdhouse hole) must **never** rely on `#22160C` or `#262428` alone. Every dark subject must feature a lit rim/midtone ramp (`>= 1.8:1` contrast ratio against `0xFF2B251D`, e.g. weathered rogue leather `#7A5838` / `#5C4028` with `#9E7850` moonlit rim; slate-blue crow plumage `#586578` / `#3E4856` with `#7C8DA3` wing sheen; warm rimmed recess `#4A3B2C` around dark openings).

### 2D. Piskel MCP Authoring Workflow (resolves S12)
In Step 4 (BUILD), the builder uses the **Piskel MCP (`C:\Dev\Tools\piskel-mcp-server`)** to author and preview sprites/scenes with indexed palettes whose hex colours map 1:1 to `SpriteTones` role chars (`o,h,b,s,a,d,l,m,k,g`) or `SceneGrid` palette chars, then exports/transcribes them into `Icons.kt` and `EmptyStates.kt` where `SpriteGridTest` and `SceneGridTest` verify 100% lossless char mapping and outline enclosure.

---

## 3. Target 1 — App Icon & Setup Banner Informant Crest (resolves B4, B6, S1, S6, S17)

### 3A. Master `36 × 36` Informant Crest Grid (`EmptyStates.kt`)
- Expose `EmptyStates.INFORMANT_CREST_GRID: SceneGrid` (`36 × 36`) and `EmptyStates.drawInformantCrest(canvas: PixelCanvas)` (**resolves B7**).
- **Safe-Circle Composition (resolves B6)**:
  - Android's adaptive icon safe circle has diameter `66/108` = `22` logical pixels centered at `(18, 18)` (radius `11` px; inscribed square `x = 10..25, y = 10..25`).
  - Keep the **entire core silhouette** inside the safe circle (`x = 7..28, y = 7..28`, with corners tapered inside radius `11` of `(18, 18)`):
    - **Left (`x = 7..12, y = 10..26`)**: Bevelled stone pillar edge (`#8A7E68` light, `#665C4B` mid, `#474034` shadow) framing the informant.
    - **Center (`x = 11..22, y = 8..27`)**: Hooded OSRS informant bust in moonlit rogue leather (`#9E7850` lit hood rim, `#7A5838` mid cowl, `#4E3622` inner fold, `#2A1C12` shadow hollow), smirking golden-amber eye glint (`#FFD866`), and gloved hand (`#D8A878`) cupped beside the mouth.
    - **Right (`x = 21..28, y = 10..25`)**: Glowing gold sync scroll (`#F2DFB6` / `#C8A870` parchment, `#FFD866` / `#E5A93B` gold rollers) with two cyan magic whisper motes (`#78D4E4` / `#48A8B8`) arcing from the cupped hand to the scroll.

### 3B. Adaptive Launcher Drawables & Legacy Mipmaps (resolves B6, S6)
1. **`app/src/main/res/drawable/ic_launcher_foreground.xml`**:
   - Generated directly from `EmptyStates.INFORMANT_CREST_GRID` (`36 × 36` mapped to `108 × 108` viewport at `3×3` units per pixel) using only axis-aligned `M x,y h w v h h -w Z` rectangles.
2. **`app/src/main/res/drawable/ic_launcher_monochrome.xml` (resolves B6 monochrome blobbing)**:
   - Dedicated Android 13+ monochrome vector (`108 × 108` viewport) rendering the crisp silhouette of the hood + cupped hand + scroll with negative-space cutouts for the glinting eye, pillar seam, and scroll seal.
   - Update `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml` so `<monochrome android:drawable="@drawable/ic_launcher_monochrome" />`.
3. **Legacy API 24–25 Mipmap PNGs**:
   - Regenerate `app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher.png` and `ic_launcher_round.png` from `EmptyStates.drawInformantCrest` over the stone plaque background so API 24–25 devices match the new icon.

### 3C. Setup Screen Integration (`SetupScreen.kt` — resolves B4, S1, N9)
- Add `@Composable fun InformantCrest(modifier: Modifier = Modifier, scaleMultiplier: Int = 2)` in `EmptyStates.kt`:
  - Renders `36 × 36` `EmptyStates.drawInformantCrest` at `scale = computeArtPx(density) * scaleMultiplier` inside a `(36 * scaleMultiplier).dp` (`72.dp` at `k = 2`) box with `contentDescription = "Pssst informant crest"` (**resolves S1, N9**).
- In `SetupContent` (`app/src/main/java/com/pssst/ui/setup/SetupScreen.kt`, line 145, right above `TitlePlaque`):
  - Render `InformantCrest(scaleMultiplier = 2)` inside a recessed `Box(Modifier.pixelBevelBox(raised = false).padding(6.dp))`, followed by `Spacer(Modifier.height(12.dp))`, then the existing `TitlePlaque(title = "Welcome to Pssst", ...)`.

---

## 4. Target 2 — 7 Empty-State Scenes (`EmptyStates.kt` — resolves S1, S13, N1, N2, N4)

### Resolution & Integer Art-Pixel Scaling (`48 × 36`, exact 4:3)
- Upgrade `EmptyStates.SCENE_WIDTH = 48` and `EmptyStates.SCENE_HEIGHT = 36`.
- **Mixel-Free Scaling (resolves S1)**:
  - Update `EmptyStateVignette` in `EmptyStates.kt` so that instead of arbitrary non-multiple `Dp` sizes, it scales each scene texel by an integer multiple of `computeArtPx(density)`:
    - Default `size: Dp = 96.dp` (`96.dp × 72.dp`, which is `2 * artPx` per texel—matching the exact `2dp` texel size of the rest of the app!).
    - In `OsrsEmptyState`, pass `size = 96.dp`.
    - In `GoalCard.kt` (compact card row), pass `size = 48.dp` (`48.dp × 36.dp`, `1 * artPx` per texel).
    - Compute `val targetScale = (size.value / EmptyStates.SCENE_WIDTH).roundToInt().coerceAtLeast(1) * computeArtPx(density)` capped by `min((this.size.width / sceneW).toInt(), (this.size.height / sceneH).toInt()).coerceAtLeast(1)`.
- Update KDoc comments in `EmptyStates.kt` from `"32x24"` to `"48x36"` while keeping enum constant names unchanged (**resolves N1**).

### Focused Scene-by-Scene Specification (`48 × 36` each — resolves S13, N2, N4)

1. **`CLOSED_CHEST` (Bank Empty)**:
   - **Left (`x = 2..21, y = 6..33`)**: Snoozing OSRS Bank Clerk in blue-grey vest (`#58748C` / `#3E5366`) and crisp bone collar (`#E8DEC5`) slumped asleep on an oak teller counter (`#9C7848` / `#70532E`), head resting on crossed forearm, with three stepped pixel `'Z'` `'z'` `'z'` glyphs (`#FFE0A0` inside `#0C0906` outline) floating up-left.
   - **Right (`x = 22..45, y = 10..33`)**: Open oak-and-iron bank chest with warm-rimmed dark interior (`#4A3B2C` rim around `#1A140E` hollow), pale cobweb (`#C8C0B0`) across the open lid corner, and a bright cream moth (`#F2DFB6` / `#E5A93B`) fluttering up out of the chest.

2. **`FALLOW_PATCH` (No Active Crops)**:
   - **Left (`x = 4..23, y = 4..33`)**: Grumpy OSRS Tool Leprechaun sitting on an upright iron compost bucket (`#8A8E94` / `#5C6066` iron staves with dark heaped compost rim) — tall buckled green top hat (`#4CAF50` / `#2E6F32`), ginger beard (`#D86828`), crossed arms, and scowling brow.
   - **Right (`x = 24..44, y = 6..33`)**: Iron farming rake stuck into dry furrowed soil ridges (`#7A5C3C` / `#523C26`) with a slate-blue crow (`#7C8DA3` wing sheen, `#586578` body, `#E5A93B` beak) perched atop the angled rake handle eyeing a sprout of weeds below (**resolves S13, N2, N4**).

3. **`EMPTY_BIRDHOUSE` (No Birdhouses)**:
   - **Center (`x = 10..32, y = 10..34`)**: Warm redwood birdhouse (`#A85038` / `#7C3624` / `#542216`) with pitched roof overhang, brass roof studs, recessed entrance hole (`x = 18..24, y = 19..25` with `#5C4028` inner bevel so it contrasts cleanly), and wooden post.
   - **Top (`x = 13..29, y = 1..13`)**: Clockwork bird (`#7898B8` / `#4E6882` steel plumage, bright brass wind-up key `#FFD866` / `#E5A93B` on back) clinging upside-down on the roof eaves, head craned down peering into the entrance hole.
   - **Right (`x = 35..43, y = 12..23`)**: Bold pixel `?` glyph in `#FFE0A0` (`OsrsTextPrimary`) with `#0C0906` outline (zero overlap with birdhouse at `x <= 32`, **resolves N2**).

4. **`POTION_RACK` (Potion Storage Empty)**:
   - **Shelf (`x = 3..44, y = 21..25`)**: Oak alchemy plank (`#9C7848` / `#70532E`) with iron wall brackets (`#7A7E85`).
   - **Left (`x = 6..19, y = 10..20`)**: Carved stone mortar (`#8C8270` / `#635B4E`) with angled wooden pestle (`#B58E58`).
   - **Right (`x = 22..41, y = 11..33`)**: Tipped-over glass vial (`#A8D0D8` / `#68929C`) with popped cork and glowing green herblore liquid (`#6CD466` / `#38984C`) dripping off the plank edge (`y = 22..33`) into a falling droplet.

5. **`QUEST_SCROLL` (No Matching Quests)**:
   - **Post & Scroll (`x = 9..38, y = 2..34`)**: Weathered parchment notice (`#E8D5B0` / `#C4AC82` / `#96805C`) pinned to a rough tavern oak post (`#70532E` / `#4E3721`).
   - **Top Pin (`x = 20..39, y = 2..14`)**: OSRS Rune dagger (`#78D4E4` highlight, `#48A8B8` blade, `#286470` shadow, `#8C7A5B` rimmed guard/hilt) pinning the upper corner.
   - **Bottom Seal (`x = 17..29, y = 21..32`)**: Blue Quest Star wax medallion (`#68A2F2` / `#2E6BC6` / `#193E7A`) with gold star center (`#FFD866`).

6. **`SUNDIAL` (Dailies Complete / Empty — simplified per S13)**:
   - **Left-Center (`x = 6..29, y = 3..33`)**: Carved stone sundial pedestal (`#9E9482` / `#756C5E` / `#524B40`) with brass gnomon (`#E5A93B`) and a wooden plank sign (`#70532E` / `#4E3721`) hung across the pillar displaying crisp 5px-tall pixel text `"RESET"` in `#FFE0A0`.
   - **Right (`x = 30..44, y = 18..33`)**: Open, empty oak battlestaff barrel / herb chest with hinged lid propped open and one stray herb leaf beside it.

7. **`LEDGER_BOOKMARK` (No GP Goal Set — simplified per S13)**:
   - **Pedestal (`x = 8..39, y = 21..34`)**: Chiseled stone plinth (`#8C8270` / `#635B4E`) topped with a crimson velvet display cushion (`#C43B32` / `#8F241E`) and gold corner tassels (`#FFD866` / `#E5A93B`).
   - **Ghost Item (`x = 13..34, y = 3..20`)**: Crisp chalk-drawn ghost silhouette (`#F2DFB6` / `#9E9580` 2px-stepped dashed outline over `#0C0906`) of a tall **OSRS GP Coin Stack & Crowned Trophy** floating above the empty velvet cushion.
   - **Price Tag (`x = 30..44, y = 22..32`)**: Brass price tag (`#E5A93B` / `#B58024`) tied to the cushion corner with a bold `?` glyph.

---

## 5. Target 3 — 16×16 UI, Navigation, Chip & Status Sprites (`Icons.kt`, `Ornaments.kt`, `Tables.kt`)

### Call-Site Retint Strategy (resolves B2, B3, S15, S16)
- **Sprites #1–12 (`OverviewShield` .. `TreeCrown`)**: Designed as **retint-safe** first (every material boundary is separated by a 1px `'o'` seam or `'a'`/`'d'` contrast so `GOLD` and `STONE` rows 2–5 in `sprites.png` read cleanly), while also using `l/m/k/g` secondary roles in `defaultNaturalTones` for rich natural renders in rows 0–1.
- **Sprites #13–16 (`BadgeReady`, `BadgeGrowing`, `BadgeEmpty`, `BadgeWarning`)**:
  - **Call-site update in `Tables.kt` (line 201)**: Change `OsrsTableStatusCell` to pass `tones = icon.defaultNaturalTones ?: ...` so the 4 status badges render with their bespoke natural presets (`HERB`, `TERRACOTTA_SPROUT`, `STONE`, `BLIGHT_WARNING`) in all tables!
  - **Distinct Silhouettes (CVD-safe, resolves B3 & S16)**:
    - `BadgeReady`: Upright **3-lobed Ranarr leaf fan** + gold top sparkle (`g`/`a`).
    - `BadgeGrowing`: **Tipping brass watering can spout (top-left, `a`/`d`) pouring cyan water drops (`l`/`m`) onto a green sprout (bottom-right, `h`/`b`/`s`)** — matches brief "tipping watering can over sprout".
    - `BadgeEmpty`: **Low horizontal weathered stone ring / hollow pot rim** (`h`/`b`/`s` with dark soil `d` center).
    - `BadgeWarning`: **Slumped, downward-curled blighted leaf hook (bottom-right, `h`/`b`/`s`) paired with a bold vertical `!` / buzzing fly pair (top-left, `a`/`l`)** using `SpriteTones.BLIGHT_WARNING`.

| # | Sprite Name | OSRS World-Flavor Design (`16×16`) | Preset (`defaultNaturalTones`) | Roles Used (`o/h/b/s/a/d/l/m/k/g`) | Call-Site Mode |
|---|---|---|---|---|---|
| 1 | `OverviewShield` | Asymmetric OSRS **Heraldic Kite Shield** with notched corners, `'o'`-divided quartered face, and central boss. | `SpriteTones.GOLD` | Body (`h/b/s`), Iron quarters & boss (`a/d`), Specular glint (`g`) | Retint (`GOLD`/`STONE`) |
| 2 | `StorageChest` | **3/4-angle OSRS Bank Chest** with arched oak lid, `'o'`-bordered iron straps, and front padlock plate. | `SpriteTones.WOOD` | Oak (`h/b/s`), Iron straps & lock (`a/d`), Gold lock glint (`g`) | Retint (`GOLD`/`STONE`) |
| 3 | `ExchangeCoins` | **OSRS Grand Exchange Coin Stacks**: tall stacked gold coin column on left, smaller cascading stack on right with sparkle. | `SpriteTones.GOLD` | Gold coins (`h/b/s`), Rim shadow (`d`), Sparkle (`a/g`) | Retint (`GOLD`/`STONE`) |
| 4 | `ProgressStar` | **8-Point Compass Star** with long N/S/E/W blades and `'o'`-separated inner diamond facets. | `SpriteTones.QUEST_BLUE` | Blade ramp (`h/b/s`), Gold center facets (`a/d`), Glint (`g`) | Retint (`GOLD`/`STONE`) |
| 5 | `TimersHourglass` | **Ornate Alchemy Hourglass**: carved oak top/bottom caps (`a/d`), glass bulbs (`h/b/s`) with trickling sand in a 4px-wide neck. | `SpriteTones.GOLD` | Bulbs/sand (`h/b/s`), Frame caps & pillars (`a/d`), Glint (`g`) | Retint (`GOLD`/`STONE`) |
| 6 | `DrawerLedgerMenu` | **Leather Ledger with 3 Horizontal Brass Clasps**: keeps 3 prominent horizontal bar clasps across the face so the hamburger menu affordance is instant (**resolves S15**). | `SpriteTones.LEATHER` |3 prominent horizontal bars (`h/b`), Recessed leather backing (`s/d`), Clasp tips (`a`) | Retint (`GOLD`) |
| 7 | `EditQuill` | **Sweeping Feather Quill** with curved vane, central shaft seam, and chiseled metallic nib (no cluttering inkwell, **resolves S15**). | `SpriteTones.PARCHMENT` | Feather vane (`h/b/s`), Gold/iron nib (`a/d`) | Retint (`GOLD`) |
| 8 | `Gear` | **Dwarven Keldagrim Cogwheel**: chunky 8-tooth bronze gear with bevelled inner ring and hollow `'o'` axle center. | `SpriteTones.STONE` | Bronze/stone teeth (`h/b/s`), Inner rim rivets (`a/d`) | Retint (`GOLD`) |
| 9 | `Check` | **Heavy Embossed Checkmark**: bold calligraphic tick with serifed foot and chiseled upward blade (`HERB` green ramp, **resolves S15**). | `SpriteTones.HERB` | Emerald body (`h/b/s`), Specular edge (`a`) | Retint (`HERB`) |
| 10 | `Birdhouse` | **Redwood Birdhouse on Post**: pitched overhanging roof (`a/d`), box body (`h/b/s`), dark `'o'` entrance hole, and post. | `SpriteTones.WOOD` | Redwood body (`h/b/s`), Roof & perch (`a/d`) | Retint (`WOOD`/`STONE`) |
| 11 | `HerbSprig` | **3-Leaf Ranarr Sprig**: curved stem with 3 asymmetric serrated leaflets and root bulb. | `SpriteTones.HERB` | Leaflets (`h/b/s`), Stem & veins (`a/d`) | Retint (`HERB`/`STONE`) |
| 12 | `TreeCrown` | **Gnarled Oak Tree**: rounded multi-cluster leafy canopy (`h/b/s`) over a sturdy flared oak trunk (`a/d`) (**resolves N6**). | `SpriteTones.HERB` | Canopy (`h/b/s`), Oak trunk (`a/d`) | Preview (`HERB`) |
| 13 | `BadgeReady` | **Bursting 3-Lobed Ranarr Crown**: upright trefoil fan (`h/b/s`) with bright gold harvest sparkle at top (`a/g`). | `SpriteTones.HERB` (with gold `a/d`) | Herb fan (`h/b/s`), Gold sparkle (`a/d/g`) | Natural (`Tables.kt` uses `defaultNaturalTones`) |
| 14 | `BadgeGrowing` | **Tipping Watering Can Spout over Sprout**: top-left brass spout (`a/d`) pouring cyan droplets (`l/m`) onto bottom-right green sprout (`h/b/s`). | `SpriteTones.TERRACOTTA_SPROUT` | Sprout (`h/b/s`), Spout (`a/d`), Water drops (`l/m/k`) | Natural (`Tables.kt` uses `defaultNaturalTones`) |
| 15 | `BadgeEmpty` | **Weathered Empty Fallow Ring**: low horizontal stone/clay ring (`h/b/s`) enclosing dark fallow soil (`d/o`). | `SpriteTones.STONE` | Stone ring (`h/b/s`), Soil pit (`a/d`) | Natural (`Tables.kt` uses `defaultNaturalTones`) |
| 16 | `BadgeWarning` | **Slumped Blighted Leaf + Warning `!` / Flies**: curled drooping leaf on bottom-right (`h/b/s`) with high-contrast bone/gold `!` & fly specks on top-left (`a/l`). | `SpriteTones.BLIGHT_WARNING` | Blighted leaf (`h/b/s`), Warning fly/mark (`a/d/l/m`) | Natural (`Tables.kt` uses `defaultNaturalTones`) |

### `Ornaments.WatermarkScaleSprite` (`16×16` — resolves S8)
- Refine `WatermarkScaleSprite` in `Ornaments.kt` with a sculpted finial, curved balance beam horns, chains, and twin scale pans, validated by `SpriteGridTest`.

---

## 6. Target 4 — Overview Kingdom & Slayer Vignettes (`EmptyStates.kt` + `TimersScreen.kt` + `StateDiffAnalyzer.kt`)

### 6A. Shared Miscellania Thresholds & Unified Composition (resolves B5, B7, S1, S14, N8, N9)
- Define shared constants in `EmptyStates.kt` (or `TimersScreen.kt`) used by `MiscellaniaCard` and `StateDiffAnalyzer.kt`:
  - `const val MISCELLANIA_APPROVAL_WARN_PERCENT = 90`
  - `const val MISCELLANIA_COFFER_WARN_GP = 750_000L`
  - Update `MiscellaniaCard` lines 436 and 483 (`TimersScreen.kt`) from `>= 75` to `>= MISCELLANIA_APPROVAL_WARN_PERCENT` (`90`) so the approval text/bar colour and vignette warning state always agree!
- **Unified `32 × 24` Composition (`EmptyStates.drawKingdomVignette(canvas: PixelCanvas, isWarning: Boolean)`)**:
  - Both states share the exact same layout so state changes read at a glance (**resolves S14**):
    - **Left (`x = 2..15, y = 3..22`)**: Bust of **Advisor Ghrim** (`#D8A878` face, `#B5AE9E` / `#7A7365` balding grey hair & beard, royal shoulder trim).
      - In **`KINGDOM_HEALTHY`**: Content expression with a small gold crown emblem above.
      - In **`KINGDOM_WARNING`**: Wide worried eyes (`#FFE0A0` / `#0C0906`), furrowed brow, and bright cyan sweat droplets (`#78D4E4` / `#48A8B8`) flying off his temple.
    - **Right (`x = 16..30, y = 4..22`)**: Iron-bound Royal Coffer topped with the Miscellania Royal Crown.
      - In **`KINGDOM_HEALTHY`**: Coffer overflowing with shimmering gold GP coins (`#FFD866` / `#E5A93B`) and upright jeweled golden crown (`#B83228` velvet cap).
      - In **`KINGDOM_WARNING`**: Coffer hollow/cobwebbed (`#C8C0B0` web over `#4A3B2C` rimmed interior) with the tarnished crown tilted and cracked down the middle.
- **Composable & Placement in `MiscellaniaCard` (`TimersScreen.kt`)**:
  - `@Composable fun KingdomStatusVignette(isWarning: Boolean, modifier: Modifier = Modifier, scaleMultiplier: Int = 2)`:
    - Size is `(32 * scaleMultiplier).dp × (24 * scaleMultiplier).dp` (`64.dp × 48.dp` at `k = 2`), scaled at `computeArtPx(density) * scaleMultiplier`, with cache key `"kingdom_vignette_$isWarning"` and decorative semantics (no `contentDescription`).
  - In `MiscellaniaCard` (`CofferTab.KINGDOM` branch, lines 480–501): wrap the `KINGDOM` tab body in a `Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp))` with `KingdomStatusVignette(isWarning = miscellania.approvalPercent < 90 || miscellania.cofferAmount < 750_000L, scaleMultiplier = 2)` inside a recessed `pixelBevelBox(raised = false)` on the left and the progress bar + `"Royal Coffer"` row in a `Column(Modifier.weight(1f))` on the right.

### 6B. Slayer Task Emblem Vignette (`32 × 24` — resolves B5, B7, S1, N8, N9)
- **Pure draw function**: `EmptyStates.drawSlayerVignette(canvas: PixelCanvas, isGilded: Boolean)` (`32 × 24`).
  1. **`SLAYER_ACTIVE` (`isGilded = false`)**:
     - Horned bone monster skull (`#E8DEC5` / `#B5A88C` / `#786E58`) with glowing crimson eye sockets (`#D93829`), pierced diagonally from top-right to bottom-left by a steel broadsword (`#C8D0D8` / `#8A929A` / `#586068`) with brass crossguard and crimson hilt.
  2. **`SLAYER_COMPLETE` (`isGilded = true`)**:
     - Same sword-through-skull silhouette, but the blade and skull crown are gilded in radiant gold (`#FFD866` / `#E5A93B` / `#B58024`) surrounded by floating gold mastery sparks.
- **Gilded State Semantics (resolves B5)**:
  - `val isGilded = (!slayer.hasTask && (slayer.points > 0 || slayer.streak > 0)) || (slayer.hasTask && slayer.progress >= 1f)`
  - When a player has Slayer progress (`points > 0` or `streak > 0`) and `amount == 0` (`!slayer.hasTask`), their task is complete / ready for a new Slayer Master assignment, triggering the glowing gold emblem. Fresh/uninitialized accounts (`!hasTask && points == 0 && streak == 0`) render the standard non-gilded emblem.
- **Composable & Placement in `SlayerTaskCard` (`TimersScreen.kt`)**:
  - `@Composable fun SlayerEmblemVignette(isGilded: Boolean, modifier: Modifier = Modifier, scaleMultiplier: Int = 1)`:
    - Size is `(32 * scaleMultiplier).dp × (24 * scaleMultiplier).dp` (`32.dp × 24.dp` at `k = 1`), cache key `"slayer_vignette_$isGilded"`.
  - In `SlayerTaskCard` (`TimersScreen.kt`):
    - In the header row (line 625), replace `BulletGlyph(size = 7.dp, color = OsrsGold)` with `SlayerEmblemVignette(isGilded = isGilded, scaleMultiplier = 1)` (`32.dp × 24.dp`) so the Slayer emblem is always visible on both active and completed tasks while keeping the `42.dp` monster `ItemIcon` slot intact!
    - Additionally, when `!slayer.hasTask`, render `SlayerEmblemVignette(isGilded = isGilded, scaleMultiplier = 1)` inside the `42.dp` slot in place of the fallback question-mark `ItemIcon`.

---

## 7. Export, Contact Sheets & New Guardrail Tests (resolves S3, S6, S8, S9, S10, S11)

1. **`ArtSheetExportTest.exportScenesSheetPng()` (resolves S11)**:
   - Lay out `scenes.png` in two clean row groups at `scale = 4`:
     - **Group 1 (top)**: The 7 `EmptyStateKind` scenes (`48 × 36` each, in `56 × 44` padded cells, 4 columns × 2 rows).
     - **Group 2 (bottom)**: `InformantCrest` (`36 × 36`), `KINGDOM_HEALTHY` (`32 × 24`), `KINGDOM_WARNING` (`32 × 24`), `SLAYER_ACTIVE` (`32 × 24`), `SLAYER_COMPLETE` (`32 × 24`) in a 5-column row below Group 1.
2. **`ScreenCaptureExportTest.kt` & `ArtPacketPreviews.kt` (resolves S10)**:
   - Add two new screen captures to `ScreenCaptureExportTest.exportAllPhoneScreensToPng()` (exported to `build/art-sheet/screens/`):
     - `"16_empty_vignettes"` -> `PreviewEmptyStateVignettes()` (renders all 7 `48×36` empty state vignettes together at phone density; adjust `PreviewEmptyStateVignettes` to a 2-column or 3-column grid so `96.dp × 72.dp` vignettes fit within `380.dp` width without clipping!).
     - `"17_overview_vignettes"` -> `PreviewOverviewVignetteStates()` (a new preview in `ArtPacketPreviews.kt` rendering `MiscellaniaCard` in both Healthy and Warning states + `SlayerTaskCard` in both Active and Complete/Gilded states on a single `380dp × 760dp` screen so Step 5 can inspect all 4 Overview states at real phone density!).
3. **New `SceneGridTest.kt` (`app/src/test/java/com/pssst/theme/art/SceneGridTest.kt` — resolves S2, S3, S6)**:
   - Validates all 7 `EmptyStateKind` grids (`48×36`), `INFORMANT_CREST_GRID` (`36×36`), both Kingdom grids (`32×24`), and both Slayer grids (`32×24`):
     - Exact dimensions and valid palette mapping.
     - Zero palette colours matching `OsrsGreen`, `OsrsYellow`, or `OsrsWhite` (`0xFFFFFFFF`).
     - Contrast check: every scene's top non-outline colours achieve `>= 1.8:1` contrast ratio against `PANEL_STONE_TONES[2]` (`0xFF2B251D`).
     - XML pixel-honesty check: parses `app/src/main/res/drawable/ic_launcher_foreground.xml` and asserts every `<path>` `android:pathData` contains only axis-aligned `M/h/v/H/V/Z/z` commands with coordinates/lengths divisible by `3` (no `c/C/s/S/q/Q/a/A` curves or arcs).

---

## 8. Complete File Impact List

1. `app/src/main/java/com/pssst/theme/art/PixelCanvas.kt` — Extend `SpriteTones` with `l/m/k/g` + new presets (`QUEST_BLUE`, `TERRACOTTA_SPROUT`, `BLIGHT_WARNING`); add `SceneGrid` and `PixelCanvas.drawSceneGrid`.
2. `app/src/main/java/com/pssst/theme/art/ScenePalettes.kt` (new) — Named `Int` ARGB material constants for scenes and vignettes.
3. `app/src/main/java/com/pssst/theme/art/Icons.kt` — Replace all 16 `ArtIcons` (`16×16`) per §5.
4. `app/src/main/java/com/pssst/theme/art/Ornaments.kt` — Refine `WatermarkScaleSprite`.
5. `app/src/main/java/com/pssst/theme/art/Tables.kt` — Use `icon.defaultNaturalTones` in `OsrsTableStatusCell` so the 4 status badges render in their natural multi-material palettes.
6. `app/src/main/java/com/pssst/theme/art/EmptyStates.kt` — Upgrade all 7 `EmptyStateKind` scenes to `48×36` `SceneGrid`s; update `EmptyStateVignette` to `artPx * k` scaling (`96.dp` default in `OsrsEmptyState`); add `INFORMANT_CREST_GRID` (`36×36`), `InformantCrest`, `drawKingdomVignette` / `KingdomStatusVignette` (`32×24`), `drawSlayerVignette` / `SlayerEmblemVignette` (`32×24`), and shared Miscellania threshold constants.
7. `app/src/main/java/com/pssst/ui/main/GoalCard.kt` — Pass `size = 48.dp` (`1 * artPx` per texel) to `EmptyStateVignette(EmptyStateKind.LEDGER_BOOKMARK, size = 48.dp)`.
8. `app/src/main/java/com/pssst/ui/setup/SetupScreen.kt` — Add `InformantCrest` above `TitlePlaque` in `SetupContent`.
9. `app/src/main/java/com/pssst/ui/main/TimersScreen.kt` — Integrate `KingdomStatusVignette` + `MISCELLANIA_APPROVAL_WARN_PERCENT` (`90`) in `MiscellaniaCard`, and `SlayerEmblemVignette` in `SlayerTaskCard`.
10. `app/src/main/java/com/pssst/theme/art/ArtPacketPreviews.kt` — Update `PreviewEmptyStateVignettes` layout for `96.dp`/`64.dp` vignettes and add `PreviewOverviewVignetteStates`.
11. `app/src/main/res/drawable/ic_launcher_foreground.xml`, `app/src/main/res/drawable/ic_launcher_monochrome.xml` (new), `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`, `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`, and `app/src/main/res/mipmap-*/ic_launcher*.png` — Pixel-art OSRS Informant Crest launcher icons.
12. `app/src/test/java/com/pssst/theme/art/SpriteGridTest.kt`, `SceneGridTest.kt` (new), `ArtSheetExportTest.kt`, `ScreenCaptureExportTest.kt` — Updated guardrail tests and contact-sheet / phone-screen exporters.
