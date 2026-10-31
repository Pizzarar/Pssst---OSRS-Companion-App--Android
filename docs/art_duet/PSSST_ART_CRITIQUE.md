# Pssst Pixel-Art Overhaul — Step 2 Critique (`PSSST_ART_CRITIQUE.md`)

**Author:** Claude (Opus 5.5), Step 2 CRITIQUE
**Reviews:** `docs/art_duet/PSSST_ART_PLAN.md` (Gemini, Step 1) against `docs/ART_DUET_BRIEF.md`
**Branch:** `feat/piskel-art-duet`
**Evidence:** current source (`PixelCanvas.kt`, `PixelArt.kt`, `Icons.kt`, `EmptyStates.kt`, `Ornaments.kt`, `Tables.kt`, `SetupScreen.kt`, `OverviewScreen.kt`, `TimersScreen.kt`, `DashboardScreen.kt`, `ReadyStrip.kt`, `GoalCard.kt`, `MainUiState.kt`, `StateDiffAnalyzer.kt`, launcher XMLs), the guardrail tests, and baseline sheets `app/build/art-sheet/{sprites,scenes}.png` and `screens/{01_setup,02_overview,14_empty_states}.png`. (The captures live under `screens/`, not `captures/`.)

Severity key:
- **BLOCKING**: the plan as written will not compile, will fail a guardrail, will silently render wrong, or contradicts how the code is actually used. Step 3 must resolve these.
- **SHOULD**: a real quality, UX or authenticity problem. Resolve it, or give an explicit reason for keeping it.
- **NIT**: polish or doc accuracy.

---

## 0. Verdict

The creative direction is strong, and the 4:3 scene upgrade and the char-grid scene authoring are the right calls. The plan was written against an **imagined** version of the sprite system, though, not the real one. Its role letters collide with existing roles. It assumes every sprite renders in its natural tones, when nav icons and status badges are actually retinted. It targets a Setup "P" plaque that doesn't exist, and it uses UI-model field names that don't exist. Fix the 7 BLOCKING items below, and Step 4 can build without guessing.

---

## 1. BLOCKING

### B1. The new `b` / `d` role letters collide with existing roles, and `m` doesn't exist
- **Actual legend** (`PixelCanvas.kt:32-58`, the KDoc in `Icons.kt:6-8`, and the brief itself): `o` outline, `h` highlight, **`b` = base body fill**, `s` shadow, `a` accent, **`d` = accentDark (already exists)**, `.` transparent.
- §2 of the plan claims the current roles are `h, m, s, a` and proposes `'b' → accentBright` and `'d' → accentDark`. Remapping `b` would recolour the main body of every existing sprite: all 16 `ArtIcons`, `WatermarkScaleSprite`, `BulletDiamondSprite`, and `Theme.kt` `CheckboxCheckSprite`. The §5 palette table also uses a non-existent `m` role throughout (`h/m/s`).
- `SpriteTones` lives in **`PixelCanvas.kt`**, not `PixelArt.kt`, and it stores **`Int` ARGB**, not Compose `Color`. Defaulting fields to `Color.Unspecified` won't type-check. Converting it with `.toArgb()` yields a value that is transparent or undefined, and `blit` skips `0`, so those pixels would silently vanish.
- **Required fix (Step 3):** keep `o/h/b/s/a/d` exactly as they are. Add **new, non-colliding** letters for a secondary material ramp. Suggested: `l` = secondaryLight, `m` = secondaryMid, `k` = secondaryDark, plus optionally `g` = glint/emissive. Declare them as nullable `Int?` with **fallbacks to the primary ramp**: `'l' -> secondaryLight ?: highlight`, `'m' -> secondaryMid ?: base`, `'k' -> secondaryDark ?: shadow`, `'g' -> glint ?: highlight`. Existing presets then stay byte-identical, and retinted renders degrade to today's monochrome look instead of leaving holes. Rewrite every palette column in §5 using the real letters.

### B2. Most of the 16 sprites are retinted at their call sites, so the multi-material palettes in §5 never appear
Sprites only use `defaultNaturalTones` when a caller omits `tones`. In the shipped UI:

| Sprite | Real call-site tones |
|---|---|
| `OverviewShield`, `StorageChest`, `ExchangeCoins`, `ProgressStar`, `TimersHourglass` (bottom nav) | `if (selected) GOLD else STONE` (`DashboardScreen.kt:546-588`) |
| `StorageChest` (goal-scope picker) | `GOLD` / `STONE` (`GoalCard.kt:511`) |
| `DrawerLedgerMenu`, `Gear`, `EditQuill`, `ProgressStar` (Progression) | `GOLD` |
| `HerbSprig`, `Birdhouse`, `TimersHourglass`, `ExchangeCoins` (ReadyStrip chips) | `HERB` / `WOOD` / `GOLD` / `GOLD` when ready, **`STONE` when zero** (`ReadyStrip.kt:71-99`) |
| `Check` | `HERB` |
| `BadgeReady` / `BadgeGrowing` / `BadgeEmpty` / `BadgeWarning` | `HERB` / **`STONE`** / **`STONE`** / **`WARNING`** (`Tables.kt:199-206`) |
| `TreeCrown` | **not used in production UI** (preview only) |

So "Quest blue `ProgressStar`" renders orange or grey. The "terracotta pot + cyan droplets" `BadgeGrowing` renders all grey. "Oak + gold + iron `StorageChest`" renders as one gold or stone ramp. The `sprites.png` export already shows this: rows 2–3 are GOLD and rows 4–5 are STONE.
- **Required fix (Step 3), for each sprite, choose one:**
  - **(a) Retint-safe design (recommended for nav, top-bar, ReadyStrip):** separate materials by **value and outline seams**, not hue, using only `o/h/b/s` plus the iron `a/d` that GOLD and STONE already define. With B1's fallbacks, any `l/m/k` pixels collapse onto `h/b/s`, so the design has to read in rows 2–5 of `sprites.png`, not just row 0–1.
  - **(b) Natural-tone design:** the plan must also list the **call-site change** (for example `Tables.kt` passing a per-status natural preset) and a **new named `SpriteTones` preset** for each new material set (`QUEST_BLUE`, `TERRACOTTA_SPROUT`, `BLIGHT`…). Add each preset to `SpriteGridTest.spriteToneSets_neverUseStackCountColours`.
  - Either way, Step 5 must review **all three tone rows** of `sprites.png`.

### B3. Status badges are cross-domain, not farming-only
`OsrsTableStatusCell` badges are used for **Miscellania/payments** (`TimersScreen.kt:395`: READY / WARNING = "has enough GP"), **dailies** (`:850`: READY / EMPTY = "DONE"), **birdhouses** (`:1014-1022`: READY / GROWING = "ACTIVE" or "BUILT" / EMPTY / "?"), and **farming** (`:943-946`).
- The brief's farming-flavoured badges (Ranarr leaf, watering can over sprout, fallow pot, wilted leaf with flies) would say "diseased crop" next to a coffer-payment shortfall, and "fallow pot" next to a completed daily.
- At 16 px, READY (green leaf) and WARNING (wilted leaf) would share a **leaf silhouette** and differ mostly by hue. Under `WARNING` tones that leaf is red, which is a red/green colour-vision-deficiency trap. Today's check versus triangle-with-"!" pairing is distinguishable by shape.
- **Required fix:** this is an owner-brief conflict, so the LEAD needs to choose:
  1. Keep the four badges generic (a recommended refinement: OSRS-flavoured but domain-neutral: a sealed tick, a sandglass, an empty socket ring, a cracked "!" tablet), **or**
  2. Add farming-specific variants and have `OsrsTableStatusCell` take a domain or sprite override. Note that `SpriteGridTest` asserts `ALL_SPRITES.size == 16` **exactly**, so adding sprites means updating that assertion or putting the variants in a second validated list.

  Whichever option is chosen, every badge pair must stay **silhouette-distinct**.

### B4. The Setup integration targets code that doesn't exist
- `SetupScreen.kt` lines 72–88 hold state setup (`tokenInput`, `rememberMe`…), not a "56.dp text 'P' EngravedPlaque". **No `EngravedPlaque` composable exists** anywhere in `src/main`, and there's no "P" header on the Setup screen at all (see `01_setup.png`). The header is `TitlePlaque("Welcome to Pssst", subtitle)` in `SetupContent` (`SetupScreen.kt:145-148`).
- **Required fix:** state where the crest goes in `SetupContent`. Recommended: a centred `InformantCrest` above `TitlePlaque`, with a `Spacer` between. Or extend `TitlePlaque` with an optional `leading` slot. Use only APIs that exist. Note that the crest goes into `SetupContent`, which `PreviewScreenSetup` renders, so `01_setup.png` picks it up automatically.

### B5. The Kingdom and Slayer integration uses field names that don't exist, and its states are ambiguous
- `MiscellaniaUiModel` has `approvalPercent: Int` (0–100) and `cofferAmount: Long`. **There is no `approvalRating` or `cofferCoins`**, and `0.90` would compare a percent against a fraction. `SlayerUiModel` has `amount: Int` and `hasTask = amount > 0`. **There is no `remaining`.**
- **Slayer state conflation:** `StateDto` documents that `amount == 0` with no name means **"no assignment"**. A "task complete" state isn't preserved. So `isCompleteOrZero = amount <= 0` would show the **radiant gold emblem permanently** for anyone who isn't on a task, including players who never train Slayer. The plan must state the intended meaning. Suggested: gold = `!hasTask && points > 0` ("ready for a new master"), with a dim or neutral emblem otherwise. Alternatively, use gold for `hasTask && progress >= 1f` only.
- **Threshold mismatch in the same card:** the vignette's warning uses `< 90` or `< 750_000` (matching the notifications in `StateDiffAnalyzer.kt:79,90`), but the card's own approval text and bar turn red only at **`< 75`** (`TimersScreen.kt:436,483`). At 80%, the text would be green while Ghrim sweats. Hoist one shared constant object (for example `MiscellaniaThresholds.APPROVAL_WARN_PERCENT`, `COFFER_WARN_GP`) and use it in the vignette, the card text and colour, and `StateDiffAnalyzer`, or document why they differ.
- Accounts with no Miscellania data (`0%`, `0 GP`) would always show the warning art. Either specify a third, neutral state or decide that warning is correct there.
- **Placement:** "beside the monster/master header" is underspecified. The main Slayer row already has a 42dp monster `ItemIcon` slot plus a `weight(1f)` text column and a count column. Give the exact slot and size (see S1 for sizes).

### B6. The launcher plan ignores the circular mask, the monochrome layer and legacy icons
- **Mask:** the adaptive safe zone is a **66dp-diameter circle**, not a 66×66 square. Corners of the proposed 22×22 logical-pixel area (7..28) get clipped by circular and squircle masks. The area guaranteed to be visible under every mask is about the inscribed square: ≈46.7 units ≈ **15×15 logical px**. A pillar on the left edge and a scroll on the right edge sit exactly where the mask cuts.
- **Monochrome:** `mipmap-anydpi-v26/ic_launcher.xml` uses `ic_launcher_foreground` as the **`<monochrome>`** layer as well. On Android 13+ themed icons, every opaque pixel becomes one tint, so the whole crest becomes a single blob. Add a dedicated `ic_launcher_monochrome.xml`: a hood silhouette with an eye cut-out and a scroll, built with evenOdd holes.
- **Legacy:** `minSdk = 24`, so API 24–25 devices use `mipmap-*/ic_launcher.png` and `ic_launcher_round.png`, which still show the "P". The plan has to either regenerate those PNGs (and the Play listing icon) or explicitly accept the mismatch.
- **Required fix:** design the launcher crest for a **≤16×16 logical-pixel core** inside the circle. Treat the full pillar + figure + hand + scroll scene as Setup-only (see S6).

### B7. Pure draw functions are missing for the new vignettes, so `ArtSheetExportTest` can't render them
`ArtSheetExportTest` is a plain JVM test that draws into `PixelCanvas`. It can't invoke composables. The plan only specifies composables (`InformantCrest(...)`, `KingdomStatusVignette(...)`, `SlayerEmblemVignette(...)`), yet §6C requires exporting them.
- **Required fix:** specify Compose-free entry points, for example an `ArtScenes` object with:
  - `drawInformantCrest(canvas)`
  - `drawKingdom(canvas, warning: Boolean)`
  - `drawSlayerEmblem(canvas, gilded: Boolean)`

  Alternatively, expose the `SceneGrid` data directly. The composables then become thin wrappers around these functions.

---

## 2. SHOULD

### Guardrails and tests

- **S1. Mixed texel sizes against the UI art-pixel grid.** Integer scaling holds: `EmptyStateVignette` uses `floor(size / scene)`, so every texel is a whole number of device pixels at any density. But the rest of the UI draws at `artPx = round(density)` device pixels per art pixel (≈1dp), and the plan's sizes give **non-multiples** of that:

  | Asset | Plan size | Texel at 3.0x | UI artPx | Note |
  |---|---|---|---|---|
  | Empty state (full screen) | 80dp × 60dp | 5px | 3px | mixel |
  | Empty state (GoalCard) | 64dp × 48dp | 4px (3px at 2.625x, letterboxed) | 3px | mixel |
  | Kingdom/Slayer 32×24 | 48dp × 36dp | 4px (floor of 4.5) | 3px | mixel + letterbox |
  | Crest 36×36 | 64dp | 5px (floor of 5.33) | 3px | mixel |

  The old scenes were `64dp / 32 = 2dp` per texel, a clean 2× artPx. **Recommendation:** size everything at `k × artPx` per texel, the way `PixelScene` already does (`computeArtPx(density) * mult`):
  - Empty states at **96dp × 72dp** (k = 2, the same 2dp texel as today, so scene pixels match the old ones). This also works in GoalCard, where the row's text is `weight(1f)`.
  - Kingdom and Slayer at **64dp × 48dp** (k = 2) or 32dp × 24dp (k = 1).
  - Crest at **72dp** (k = 2).

  Switch `EmptyStateVignette` (and the new composables) to compute `scale = computeArtPx(density) * k`, falling back to floor-fit only when the box is too small. The "exactly 4.0" claim in §4 holds only at exactly 3.0x density. It fails at 2.625x and 2.75x, the most common phone densities.

- **S2. Contrast against the backgrounds the art actually sits on.** Panel stone is `#221D16`/`#27211A`/`#2B251D`, stone ground is similar, and the launcher background is `#211B15`/`#2B231B`. Several planned key colours are **≈1.0–1.1:1** against these:
  - crow `#262428`/`#423E47`
  - hood/cloak `#22160C`/`#362415`/`#4E3622`
  - "dark hollow" chest interior
  - the dark birdhouse hole
  - the dark dagger hilt

  The 1px `#0C0906` outline also disappears on these grounds. Silhouettes would read as holes. Require each subject to carry a **lit rim or mid-tone mass ≥ ~2:1** against `STONE_GROUND_TONES`/`PANEL_STONE_TONES`: a moonlit rim on the hood, a blue-grey sheen on the crow's wing, and so on. Add a small `SceneContrastTest` that asserts the top-N most-used non-outline colours of each scene clear that bar against `PANEL_STONE_TONES[2]`.

- **S3. Add a `SceneGridTest`.** No existing test validates char-grid scenes. Add one that checks:
  - exact `w × h` for each grid (48×36, 36×36, 32×24);
  - every char is in the scene's palette;
  - `'.'` is transparent and `'o'` is `OsrsOuterFrame`;
  - **no palette ARGB equals `OsrsGreen`/`OsrsYellow`/`OsrsWhite`.**

  That last check matters because `ColourDisciplineTest` is a **text** grep for `0xFF00FF80`/`0xFFFFFF00`. A raw `0xFFFFFFFF` (pure white, which equals `OsrsWhite`) or an equivalent `Color(...)` would slip through.

- **S4. `ColourDisciplineTest` matches substrings anywhere in any `.kt` file, including KDoc.** New constant names like `OsrsWhiteCollar` (the bank clerk), `OsrsGreenHat` (the leprechaun) or `OsrsYellowSand` would fail the build, and so would a KDoc mentioning "OsrsWhite". Mandate names that avoid those three substrings (for example `ClerkCollarBone`, `LeprechaunHatMid`). Likewise, `PixelHonestyTest` would flag a helper named `drawCircleGrid` or anything containing `drawOval`/`drawRoundRect`.

- **S5. Where palette constants live.** All production art today references named `Osrs*` constants from `Color.kt`. Raw hex only appears in `ArtPacketPreviews.kt`. The plan introduces 60+ raw hex values scattered through scene specs. Put them in one `theme/art/ScenePalettes.kt` (Int ARGB, named per material: `RuneBlade`, `RuneBladeShadow`, `QuestSealBlue`…), shared by the scenes, the vignettes and the presets. Use the existing constants where they match: outline is `OsrsOuterFrame` = **`0xFF0C0906`**, not the plan's `#0C0905`. `OsrsInkOutline` and `OsrsParchmentLight`, cited in §1 and §3A, **don't exist**.

- **S6. Keep the launcher and Setup crest consistent.** Author one master crest grid and **generate** `ic_launcher_foreground.xml` from it (a small Gradle task or a test that writes or verifies the XML), so the two can't drift. Then add a guard test, because `PixelHonestyTest` only scans `theme/**/*.kt` and **does not cover** the XML. The guard should parse every `pathData` and assert it uses only `M/h/v/H/V/z` with integer multiples of 3, and no `a`/`c`/`q`/`s` curves. Today's foreground uses arcs (`a6,6`) and curves (`c6.5,0…`), so the check is meaningful.

- **S7. `drawSceneGrid` signature.** Use `PixelCanvas.drawSceneGrid(rows: List<String>, palette: Map<Char, Int>, x: Int = 0, y: Int = 0)`, matching `PixelCanvas`'s `Int` ARGB convention. `PixelCanvas.kt` doesn't import Compose `Color`. Throw on unknown chars, as `SpriteTones.colorFor` does. Validate row lengths at construction (wrap the grid in a `SceneGrid(rows, palette)` class with an `init { require(...) }`, mirroring `Sprite`).

- **S8. `WatermarkScaleSprite` isn't covered by `SpriteGridTest`.** The test only iterates `ArtIcons.ALL_SPRITES`. "Staying within SpriteGridTest rules" isn't enforced. Add an `Ornaments` sprite list (watermark + bullet) to the enclosure test, or a second test case.

- **S9. `SpriteGridTest` edits.** Beyond extending `allowedChars`:
  - the body-pixel check on line 33 (`ch == 'h' || 'b' || 's' || 'a' || 'd'`) must also include the new letters, or they escape the enclosure rule;
  - `spriteToneSets_neverUseStackCountColours` must iterate the new roles and the new presets.

  Note that the plan's "≥ 24 `.` and ≥ 12 `o`" counts are **not** existing invariants. They're fine to add as new assertions, but label them as new.

### Export and review coverage

- **S10. Step 5 can't see the Kingdom vignette, or six of the seven empty states, at phone density.**
  - `02_overview.png` is cut off at the Slayer card, so `MiscellaniaCard` is below the fold.
  - `14_empty_states.png` shows only `FALLOW_PATCH`.

  Add captures to `ScreenCaptureExportTest`: an overview-cards preview showing Slayer (active, none, gilded) and Miscellania (healthy, warning), plus a capture of `PreviewEmptyStateVignettes` (all 7 at production size).

- **S11. `exportScenesSheetPng` layout.** Don't force a 36×36 crest and 32×24 vignettes into 56×44 cells built for 48×36. Drive the sheet from a list of `(label, w, h, draw)` entries and lay out by row group:
  - row group 1: the 7 empty states;
  - row group 2: the crest and the 2 + 2 vignettes.

  Keep `scale = 4`. The sprites sheet comment "3 rows of 8 for NATURAL" is stale (it's 2); fix it while you're in there.

- **S12. The Piskel pipeline is missing from the plan.** The brief requires Piskel MCP in Step 4, but the plan only describes hand-typed char grids. Specify the round trip:
  1. Author with an indexed Piskel palette whose colours **map 1:1 to role letters** (sprites) or palette chars (scenes).
  2. Export.
  3. Convert to a char grid with a script or test that **fails on any unmapped colour**.

  Otherwise the export can quantise into near-miss colours that aren't valid roles.

### Composition and authenticity

- **S13. The scenes are overstuffed for 48×36.** Each scene should have **one focal subject plus at most one supporting prop**. Current overloads:
  - `SUNDIAL`: barrel + sundial + gnomon + sign with "RESET"/"00:00" + herb box + orb + leaf.
  - `LEDGER_BOOKMARK`: plinth + cushion + tassels + Twisted Bow ghost **crossed with** a GP stack + price tag.
  - `FALLOW_PATCH`: leprechaun + bucket + rake + furrows + weeds + crow.

  Suggested cuts:
  - **SUNDIAL:** the sundial with a hanging "RESET" plank is the focus. Keep either the barrel or the herb box, not both. "00:00" and "RESET" won't both fit legibly; 5 glyphs × 4px with a 1px gap = 24px wide at 5px tall. Pick one.
  - **LEDGER_BOOKMARK:** pick one ghost item; the GP stack is clearer for "no GP goal". A 1px alternating "dotted chalk" outline of a thin bow breaks into noise, so use a **solid** 1px chalk line, with dashes only every 2px if at all.
  - **FALLOW_PATCH:** drop the crow, or make it the only character beside the rake.

- **S14. The Kingdom pair should share a composition.** Healthy (coffer + crown) and warning (Ghrim + sweat + cracked crown + cobwebbed coffer) are different scenes, so the change of state doesn't read at a glance, and four subjects in 32×24 is too many. Use the **same coffer and crown layout** for both. Warning = near-empty coffer, crown tilted and cracked, one sweat drop or a cobweb. If Ghrim stays, show him as a small bust peeking over the coffer, the same way in both states. The Slayer pair (same silhouette, gilded) already does this well; match it.

- **S15. Nav and top-bar affordances.**
  - `DrawerLedgerMenu` is the **hamburger** button. A leather journal loses the universally understood three-bar affordance, so keep three horizontal clasps or straps as the dominant read.
  - `Check` is the done-editing and checkbox glyph; the plan's "gold leaf" text contradicts its "herb/emerald" palette, so pick one. `Check` renders with `HERB`.
  - `EditQuill`: the inkwell adds clutter at 16 px for a top-bar button, so keep the quill dominant.

- **S16. The brief and plan disagree on `BadgeGrowing`.** The brief says "tipping watering can over sprout". The plan row title keeps "Watering Can", but the description is a terracotta pot with droplets. Resolve this under B3.

- **S17. The launcher changes brand continuity.** The current "P" plaque deliberately matches `CompanionPlugin.panelIcon` (`#C8A24A`/`#E8912D`, per the XML comment). The owner said the plugin needs no visual work, so the app icon and plugin icon will now differ. Flag this to the owner; it's not a blocker.

---

## 3. NIT

- **N1. Enum names.** `CLOSED_CHEST` will show an **open** chest, and `SUNDIAL` and `LEDGER_BOOKMARK` will show different subjects. Keep the enum names for call-site stability (Bank, Potions, Progression, Timers ×3, Storage, GoalCard), but update every KDoc ("Code-drawn 32x24…" in `EmptyStates.kt:56,69,108,156`).
- **N2. Plan coordinate overlaps.** In `EMPTY_BIRDHOUSE`, the house spans `x = 12..34` and the `?` starts at `x = 34`. In `FALLOW_PATCH`, the crow perches "on the stone patch border", but the scene defines no border.
- **N3. Launcher palette count.** §3A says "11 colours" but lists 13.
- **N4. Ultracompost.** An **upturned** bucket hides its contents, so "Ultracompost" can't be identified, and OSRS buckets are grey iron, not "green/gold". Either show it upright with dark compost heaped up, or call it a plain bucket.
- **N5. `ProgressStar` "iconic OSRS Quest Point Star / 8-point compass rose".** This is moot under GOLD/STONE retint (B2). Drop the "iconic" claim unless it's checked against a reference.
- **N6. `TreeCrown`.** It's unused in production UI, so spend the least effort here, and pick either Magic or Oak, not "Magic/Oak".
- **N7. `GuardrailsTest`.** It scans `kt`/`java`/`xml` in `src/main`. Don't paste wiki reference URLs into KDoc or XML comments while authoring.
- **N8. Cache keys.** New vignettes need state in the `PixelCacheKey` (for example `"kingdom_${warning}"`), so the healthy and warning bitmaps don't alias.
- **N9. Semantics.** The approval and slayer text already carries state, so mark the new vignettes as decorative (no `contentDescription`) to avoid duplicate announcements. Give `InformantCrest` a description ("Pssst informant crest").
- **N10. Plan file references.** `SpriteTones` and `Sprite` live in `PixelCanvas.kt`. `MiscellaniaCard` and `SlayerTaskCard` only render on Overview (`OverviewScreen.kt:110-111`), not "the Timers/Dailies view". The captures directory is `build/art-sheet/screens/`.
- **N11. `ArtContrastTest` and `MaterialToneTest`.** The plan's reading is correct. They only guard `Textures.*` ramps and text-on-texture contrast, so they're unaffected as long as `Textures.kt`, `Frames.kt`, `Plaques.kt` and `Tables.kt` ramps stay untouched. They give **no** protection to sprite or scene art, which is why S2 and S3 are needed.

---

## 4. Step 3 checklist (for Gemini REVISE)

1. [ ] B1: keep `o/h/b/s/a/d`. Add non-colliding `l/m/k(/g)` as `Int?` with primary-ramp fallbacks, in `PixelCanvas.kt`. Rewrite the §5 palette columns.
2. [ ] B2: for each of the 16 sprites, mark **retint-safe** or **natural + call-site change + named preset**.
3. [ ] B3: the LEAD or owner decides between generic and domain-specific status badges. Keep badge silhouettes distinct and CVD-safe. Handle the `ALL_SPRITES.size == 16` assertion.
4. [ ] B4: place the crest in `SetupContent` around `TitlePlaque`, using existing APIs.
5. [ ] B5: use the real fields (`approvalPercent`, `cofferAmount`, `amount`/`hasTask`/`points`). Define the Slayer gold-state semantics. Share the threshold constants. Handle the no-data state. Give exact placement.
6. [ ] B6: launcher core ≤16×16 logical px inside the circle; a dedicated monochrome layer; a decision on the legacy mipmaps.
7. [ ] B7: Compose-free `draw*` functions or `SceneGrid`s for the crest, Kingdom and Slayer art.
8. [ ] S1: art-pixel-multiple sizes (96×72, 64×48, 72) with `artPx × k` scaling.
9. [ ] S2/S3/S8/S9/S6: new `SceneGridTest`, `SceneContrastTest`, watermark coverage, the `SpriteGridTest` body-role update, and the launcher pathData guard.
10. [ ] S10/S11: added screen captures and a data-driven scenes sheet.
11. [ ] S12: specify the Piskel palette-indexed round trip.
12. [ ] S13/S14/S15: simplify the scenes, unify the Kingdom composition, preserve the hamburger affordance.
