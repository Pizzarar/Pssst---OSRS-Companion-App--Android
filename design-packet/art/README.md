# Pssst Pixel-Honest Art Packet Reference (`com.pssst.theme.art`)

Every asset in this packet is generated in code into an integer [`PixelCanvas`](../../app/src/main/java/com/pssst/theme/art/PixelCanvas.kt) at low resolution (`artPx = max(2, density.roundToInt())` device pixels per art pixel) and drawn scaled with `FilterQuality.None` so every art pixel remains a crisp square. Zero external image files are bundled, and everything runs on `minSdk 24`.

---

## 1. Pixel Engine (`PixelCanvas.kt` & `PixelArt.kt`)

### `artPx()` & `Modifier.pixelArt(key) { ... }`
* **How it works:** Computes `val step = max(2, density.roundToInt())` inside `drawWithCache`, allocates a `PixelCanvas(ceil(width / step), ceil(height / step))` cached in an `LruCache<PixelCacheKey, ImageBitmap>(96)`, runs the integer pixel generator once per `(key, artWidth, artHeight)`, and draws the `ImageBitmap` scaled to `(artW * step, artH * step)` with `FilterQuality.None`.
* **Primitives (`PixelCanvas`):**
  * `set(x, y, argb)`, `fill(x, y, w, h, argb)`, `hLine(x, y, len, argb)`, `vLine(x, y, len, argb)`
  * `rectOutline(x, y, w, h, argb)` — 1-art-px rectangular outline
  * `bevelBox(x, y, w, h, outline, light, dark, fill, raised)` — 1-art-px outline + 1-art-px top/left light & bottom/right shadow bevel
  * `cutCornerStaircase(steps, bgArgb)` — 4-connected integer pixel staircase corners (replaces diagonal vector chamfers)
  * `rivet(x, y, size)` — `3×3` or `4×4` art-pixel stud with 1 highlight pixel top-left and shadow bottom-right
  * `cornerBracket(corner, arm, thick)` — L-shaped wrought-iron corner bracket with 1-art-px dark outline and rivet
  * `hingeStrap(y, side, len, h)` — horizontal wrought-iron strap with stepped spearhead tip and two rivets
  * `blit(sprite, tones, x, y)` — blits a `Sprite` character grid onto the canvas

### `PixelNoise`
* **Deterministic integer noise:** `hash(x, y, seed)`, bilinear `value(x, y, cell, seed)`, Worley `cellular(x, y, cell, seed)`, and `quantise(v255, tones, x, y)` using a `4×4` Bayer ordered dither matrix. Every output pixel is strictly a member of the material's `tones` array.

---

## 2. Procedural Materials (`Textures.kt`)

| Material Generator | Modifier | Palette Tones (3–5 max) | Construction |
|---|---|---|---|
| `Textures.drawStoneGround(canvas, seed = 101)` | `Modifier.stoneGround(seed = 101)` | `STONE_GROUND_TONES` (5): `#14110D`, `#191612`, `#1E1A15`, `#221D16`, `#2B251D` | Irregular courses (`6–10` art px) and block widths (`10–22` art px), `1-art-px` dark mortar, `1-art-px` top-left highlight edge, Bayer-dithered interior noise. Adjacent blocks never share a base tone. |
| `Textures.drawPanelStone(canvas, seed = 101)` | `Modifier.carvedStone(seed = 101)` | `PANEL_STONE_TONES` (3): `#221D16`, `#27211A`, `#2B251D` | Low-contrast Bayer-dithered value noise behind panel body text (`>= 4.54:1` contrast for all text colours including `OsrsRed`). |
| `Textures.drawOakBeam(canvas, seed = 202)` | `Modifier.oakBeam(seed = 202, showPlankSeams = true)` | `OAK_TONES` (4): `#16100B`, `#241B13`, `#2D2218`, `#36281C` | Horizontal planks (`5–9` art px high), `1-art-px` dark seam, 8:1 stretched grain, occasional `5×3` pixel knots, and optional wrought-iron end straps. |
| `Textures.drawHammeredIron(canvas, seed = 505)` | `Modifier.hammeredIron(seed = 505)` | `IRON_TONES` (3): `#0E0C0A`, `#23201D`, `#524B42` | Flat `OsrsIronBody` with sparse (`~3%`) irregular `2-pixel` light/dark hammer dimples and a `1-art-px` bevel. |
| `Textures.drawParchment(canvas, seed = 303)` | `Modifier.parchment(seed = 303, showStainVignette = true)` | `PARCHMENT_TONES` (3): `#261F16`, `#332B20`, `#3F3527` | Horizontal/vertical linen fibre streaks and Bayer-dithered edge foxing within `8` art px of the border. |
| `Textures.drawLeather(canvas, seed = 404)` | `Modifier.leather(seed = 404, stitched = true)` | `LEATHER_TONES` (3 + stitch): `#18100C`, `#281C15`, `#3A291F` + `#7A6548` | Cellular (Worley) pebble grain (`cell = 4` art px) with a single-art-px `2-on/2-off` stitch line inset `3` art px. Used only on the Settings drawer and Inventory pack tray. |

---

## 3. Frames & Hardware (`Frames.kt`)

* **`Modifier.ledgerPanelFrame(weight, seed, accentBorder)`** (aliased by `Modifier.osrsPanel`):
  * Draws `PANEL_STONE_TONES` interior, `1-art-px` `OsrsOuterFrame` border, `1-art-px` chiseled bevel (`OsrsBorderLight` top/left, `OsrsBorderDark` bottom/right), and weight-specific iron hardware:
    * `FrameWeight.LIGHT`: `3×3` corner rivets
    * `FrameWeight.MEDIUM`: `6×6` L-shaped corner brackets with rivets
    * `FrameWeight.HEAVY`: `2-art-px` bevel, inner brass rule (`OsrsBrassMid`), `7×7` corner brackets, and upper spearhead hinge straps
* **`Modifier.oakBeamFrame(seed = 202)`**: Top bar dark-oak plank beam with top/bottom hammered iron rails and corner hinge straps.
* **`Modifier.stoneSillFrame(seed = 109)`**: Bottom navigation carved-stone sill with top iron-and-brass rail and corner brackets.
* **`Modifier.ledgerWellSlot(empty, highlighted)`** (aliased by `Modifier.osrsSlot`): Recessed `bevelBox(raised = false)` well with `OsrsBorderDark` top/left inner shadow and `OsrsBorderLight` bottom/right catch-light (`OsrsGold` rim when `highlighted = true`).
* **`Modifier.parchmentNotice(accentBorder, seed)`**: Weathered dark-vellum slip with `1-art-px` outline, brass inner trim, and four `3×3` corner tacks.
* **`Modifier.leatherBoundPanel(seed = 404)`**: Pebble-grain oxblood hide panel with `2-on/2-off` stitch line and iron corner brackets.

---

## 4. Plaques & Headers (`Plaques.kt`)

* **`GoldLeafText`**: Flat `OsrsGold` (`#FF981F`) text with a hard `Shadow(color = OsrsOuterFrame, offset = Offset(1f, 1f), blurRadius = 0f)` drop shadow (no smooth gradient on glyph fills).
* **`TitlePlaque` & `Modifier.chamferedPlaqueFrame`**: Oak-and-brass plaque with 4-connected pixel staircase corners (`steps = 3`), brass inner bevel, and side `3×3` rivets.
* **`SectionHeaderPlaque`**: Pairs a `5×5` pixel `BulletGlyph`, `GoldLeafText`, optional trailing action slot, and a pixel `EngravedRule`.
* **`Modifier.bookmarkTab(selected, seed)`**: Active tab is a raised oak/brass tab with a `2-art-px` top gold bar (`OsrsGoldHighlight` + `OsrsGold`) and side rivets; inactive tab is a recessed stone slot (`OsrsSlotEmpty`).
* **`StampedNumeralBadge`**: `14×14` art-pixel recessed iron/brass die plate with `3×3` corner studs and a crisp gold numeral (replaces `WaxSealMedallion`).

---

## 5. Dividers & Ornaments (`Ornaments.kt`)

* **`EngravedRule`**: `3-art-px`-tall horizontal rule (`OsrsBorderDark` groove, `OsrsBorderLight` lower catch-light) with `5×5` pixel diamond end-caps and optional center chevron-and-diamond flourish.
* **`BulletGlyph`**: `5×5` art-pixel diamond sprite (`OsrsOuterFrame` outline, `OsrsGoldHighlight` top-left pixel, `OsrsGold` center, `OsrsGoldDark` bottom-right).
* **`IronGripHandle`**: `10×8` art-pixel wrought-iron drag grip with 3 embossed horizontal bars and side rivets.
* **`Ornaments.drawPixelScaleWatermark`**: `20×16` art-pixel low-contrast engraved balance scale inside empty Grand Exchange slots.

---

## 6. 16×16 Pixel Sprite Icons (`Icons.kt`)

Every sprite in `ArtIcons` is a `16×16` character grid (`'o'` = `OsrsOuterFrame` `#0C0906` outline, `'h'` = top-left highlight, `'b'` = base fill, `'s'` = bottom-right shadow, `'a'` = secondary accent, `'d'` = secondary shadow, `'.'` = transparent) rendered via `PixelSprite`:

| Sprite | Natural Palette | Used In |
|---|---|---|
| `ArtIcons.OverviewShield` | `SpriteTones.GOLD` | Bottom Nav (Overview) |
| `ArtIcons.StorageChest` | `SpriteTones.WOOD` | Bottom Nav (Storage) |
| `ArtIcons.ExchangeCoins` | `SpriteTones.GOLD` | Bottom Nav (Exchange) & `ReadyStrip` |
| `ArtIcons.ProgressStar` | `SpriteTones.GOLD` | Bottom Nav (Progress) |
| `ArtIcons.TimersHourglass` | `SpriteTones.GOLD` | Bottom Nav (Timers) & `ReadyStrip` |
| `ArtIcons.DrawerLedgerMenu` | `SpriteTones.GOLD` | Top Bar Drawer Button |
| `ArtIcons.EditQuill` | `SpriteTones.GOLD` | Top Bar Edit Timers Button |
| `ArtIcons.Gear` | `SpriteTones.GOLD` | `GoalCard` configure button |
| `ArtIcons.Check` | `SpriteTones.HERB` | Top Bar Done-Editing & `OsrsCheckbox` |
| `ArtIcons.Birdhouse` | `SpriteTones.WOOD` | `ReadyStrip` & `TimersScreen` |
| `ArtIcons.HerbSprig` | `SpriteTones.HERB` | `ReadyStrip` & `TimersScreen` |
| `ArtIcons.TreeCrown` | `SpriteTones.HERB` | `ReadyStrip` & `TimersScreen` |
| `ArtIcons.BadgeReady` | `SpriteTones.HERB` | `OsrsTableStatusCell` (`READY`) & `OsrsStatusBadge` |
| `ArtIcons.BadgeGrowing` | `SpriteTones.STONE` | `OsrsTableStatusCell` (`GROWING`) |
| `ArtIcons.BadgeEmpty` | `SpriteTones.STONE` | `OsrsTableStatusCell` (`EMPTY`) |
| `ArtIcons.BadgeWarning` | `SpriteTones.WARNING` | `OsrsTableStatusCell` (`WARNING`) & error banners |

---

## 7. 32×24 Empty-State Scenes (`EmptyStates.kt`)

Every empty state renders a standalone `32×24` art-pixel scene (`EmptyStateVignette`) directly on the stone ground above the screen's second-person copy:
* `EmptyStateKind.CLOSED_CHEST` — Closed oak chest with iron straps and brass padlock (`BankScreen`, `StorageScreen`)
* `EmptyStateKind.FALLOW_PATCH` — Timber-bordered furrowed soil patch with wooden stake (`TimersScreen` Patches)
* `EmptyStateKind.EMPTY_BIRDHOUSE` — Wooden birdhouse on post with dark entrance hole (`TimersScreen` Birdhouses)
* `EmptyStateKind.POTION_RACK` — Wooden shelf with two empty glass vials (`PotionStorageScreen`)
* `EmptyStateKind.QUEST_SCROLL` — Rolled parchment scroll with crimson-and-gold seal (`ProgressionScreen` Quests)
* `EmptyStateKind.SUNDIAL` — Carved stone sundial pedestal with bronze gnomon fin (`TimersScreen` Dailies)
* `EmptyStateKind.LEDGER_BOOKMARK` — Open leather-bound ledger with forked gold bookmark and coin stack (`GoalCard`)

* **`Modifier.ledgerPanelInset(weight)`**: Insets child content by `(step * (1 + bevel)).toDp()` so full-bleed children (`OsrsTableHeaderPlaque`, `OsrsTableRow`, `OsrsTableRowSeparator`) sit cleanly inside the outer frame and chiseled bevel without overwriting the side border.
* **`OsrsTableRow` (recessed darker stone band)**: Odd-indexed rows draw a `2-tone` Bayer-dithered recessed band (`OsrsSlotEmpty` `#221D16` + `OsrsPanelMid` `#27211A`) with a `1-art-px` top inner shadow (`OsrsBorderDark`) and `1-art-px` bottom catch-light (`OsrsBorderLight`).
* **`PixelItemImage` & `computeItemSpriteDrawSize`**: Draws decoded OSRS item `ImageBitmap`s with `FilterQuality.None` at an exact integer multiple when `targetPx >= srcMax * 2` (`1×` native size otherwise, or integer downscale when smaller than the source). Uncached items fall back to a recessed `pixelBevelBox(raised = false)` stone slot with 2-letter gold initials.
* **`SkillCard` & `TotalSkillCard` (`ProgressionScreen.kt`)**: Dynamic `BoxWithConstraints` recessed icon well (`32–44.dp`), level with boost/drain delta (`+4` / `-13`) or virtual level (`v104`), XP and XP remaining (`-561K` or `MAX`), combat level (`Cmb 124`), and `5.dp` pixel progress bar.
* **Picker Icons (`IconWishlist.kt` & `GoalCard.kt`)**: `OsrsFilterChip` supports an optional `leading` icon slot so bank-tab source chips show each tab's first item icon (`All Tabs` shows `ArtIcons.StorageChest`), custom GP goals show the Coins sprite (`995`), and `SnapshotIngest.missingIcons` prioritizes `IconWishlist` picker IDs plus a bounded high-value tradeable backfill (`BACKFILL_PER_SYNC = 150` until `countIcons() >= 2,000`).

---

## 8. Exporting PNG Sheets & Screen Captures (No Emulator Needed)

Run the JVM unit test suite to verify pixel honesty, tone counts, sprite grid rules, WCAG contrast, and export PNG sheets + 15 full-screen captures:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Exported PNGs are written to:
* `app/build/art-sheet/materials.png`, `hardware.png`, `sprites.png`, `scenes.png` (via `ArtSheetExportTest`)
* `app/build/art-sheet/screens/01_setup.png` … `15_goal_dialog.png` (via `ScreenCaptureExportTest`)
