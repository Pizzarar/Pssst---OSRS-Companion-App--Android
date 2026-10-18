# Pssst Art Packet — Phase 1: Audit & Art Direction

**One-line direction:** *A well-worn adventurer's ledger* — carved stone, aged dark oak, weathered parchment, worn binding leather, wrought-iron hardware, and gold-leaf lettering, entirely generated in Compose code (`minSdk 24`).

---

## 1. Screen-by-Screen Audit

### `DashboardScreen.kt` (Top Bar, Bottom Nav, Settings Drawer, Dialogs)
* **What is generic today:**
  * The top bar and bottom nav are flat `osrsPanel()` rectangles separated from the screen by plain `1.dp` horizontal lines (`OsrsOuterFrame` / `OsrsBorderLight`).
  * Bottom-nav glyphs (`DrawShieldIcon`, `DrawChestIcon`, `DrawCoinStackIcon`, `DrawStarIcon`, `DrawHourglassIcon`) are inline `Canvas` drawings with inconsistent stroke weights (`1.2.dp` to `1.8.dp`) and no shared active plaque or indicator treatment.
  * The settings drawer (`ModalDrawerSheet`) is a flat `OsrsPanelDark` column with plain text section headers (`ACCOUNT`, `NOTIFICATIONS`, `SYNC`, `DISPLAY`, `DANGER ZONE`), raw `Switch` rows, and an unframed drawer header.
* **Where flair goes:**
  * **Top Bar:** Heavy **Aged Dark Oak beam** (`Modifier.oakBeamFrame`) bound with wrought-iron edge straps and corner rivets; `"Pssst"` rendered with gold-leaf fill + crisp `1.px` hard drop shadow (`OsrsOuterFrame`); RSN subtitle set beside a chiseled diamond bullet.
  * **Bottom Nav:** Carved stone sill (`Modifier.stoneSillFrame`) with an engraved top rail, iron corner brackets, and a recessed brass-trimmed active tab well; all 5 nav icons + drawer menu/edit icons unified as `ImageVector`s in `Icons.kt`.
  * **Settings Drawer:** Bound **Worn Leather** spine and cover grain (`Modifier.leatherPanel`) with chamfered stone section plaques (`OsrsSectionPlaque`), gold-leaf headers, and iron-framed toggle rows.

### `ReadyStrip.kt` (Top Bar Quick-Status Strip)
* **What is generic today:**
  * The 4 status slots (Exchange, Birdhouses, Herb Patches, Tree Patches) are plain `osrsSlot()` boxes with inline `Canvas` strokes and raw numbers.
* **Where flair goes:**
  * Recessed **Carved Stone ledger wells** with beveled iron rims (`Modifier.ledgerWellSlot`); when `ready > 0`, a subtle warm gold-leaf inner rim highlight and unified vector glyphs from `Icons.kt`.

### `OverviewScreen.kt` (`GoalCard.kt`, `PhoneAddressCard.kt`, Wealth, Slayer, Miscellania, Quick Actions)
* **What is generic today:**
  * Every card uses the identical `osrsPanel()` weight with no visual hierarchy between the primary Goal / Total Wealth cards and secondary status cards.
  * `GoalCard.kt` lists pinned goals separated by bare `OsrsHeaderDivider` lines; the `"+"` add button is a plain square slot with a text `"+"` character; the empty prompt (`"Pin an item goal..."`) has no visual anchor.
  * `QuickActionsBanner` is a flat green-bordered box (`OsrsHerbGreen.copy(alpha = 0.08f)`) with plain text strings joined by `"  •  "`.
  * `PhoneAddressCard.kt` shows the sync URL in a plain `osrsSlot` box with a flat `"COPY"` chip.
* **Where flair goes:**
  * **Card hierarchy:** Heavy iron hinge-strapped frame (`FrameWeight.HEAVY`) for `TotalWealthCard` and `GoalCard`; standard bracketed frame (`FrameWeight.MEDIUM`) for `SlayerTaskCard`, `MiscellaniaCard`, and `PhoneAddressCard`.
  * **Section headers:** Upgraded to **Gold-Leaf Section Plaques** (`OsrsSectionPlaque`) with chiseled end-caps and engraved rule dividers.
  * **`QuickActionsBanner`:** Restyled as a **Weathered Parchment** notice strip (`Modifier.parchmentNotice`) pinned with iron tacks and diamond bullets between ready counts.
  * **`GoalCard`:** Chiseled `+` icon button in a brass-rimmed slot; parchment-tinted empty state with a small drawn ledger-bookmark vignette.
  * **`PhoneAddressCard`:** Wax-seal accent badge beside the receiver status and a recessed parchment slip for the `http://<ip>:8480/sync` address.

### `StorageScreen.kt`, `BankScreen.kt`, `InventoryScreen.kt`, `PotionStorageScreen.kt`
* **What is generic today:**
  * `StorageScreen` uses a flat `OsrsSegmentedTabs` bar (`BANK`, `INVENTORY`, `POTIONS`) and plain text for `BankSharingOff`.
  * `BankScreen` has a flat search bar, a plain `osrsPanel` total value banner, and plain centered text when empty.
  * `InventoryScreen` renders Equipment and the 28-slot Inventory inside identical `osrsPanel()` boxes with a text header.
  * `PotionStorageScreen` renders dose bars as simple colored rectangles and uses plain text when empty.
* **Where flair goes:**
  * **`OsrsSegmentedTabs`:** Upgraded into physical **Ledger Bookmark Tabs** (`Modifier.bookmarkTab`) with stitched leather/parchment tab tops, notched bottom corners on active tabs, and iron divider ticks.
  * **`BankScreen`:** Wrought-iron search field frame with a drawn magnifying-glass vector icon; engraved brass value plaque for `Total Bank Value`; **Closed Ironbound Chest** vignette (`EmptyStates.ClosedChest`) when bank data has not arrived or sharing is disabled. Dense 4-column item cells remain clean recessed stone slots (`osrsSlot`) for fast scanning.
  * **`InventoryScreen`:** Equipment panel framed with iron corner brackets (`FrameWeight.MEDIUM`); Inventory 4×7 grid framed as a **Worn Leather pack tray** (`Modifier.leatherPanel`) while individual item slots stay crisp recessed stone wells.
  * **`PotionStorageScreen`:** Vial dose gauge styled as an **engraved glass/brass tube** with tick marks for doses 1–4; **Empty Apothecary Rack** vignette (`EmptyStates.PotionRack`) when no potions are synced.

### `GrandExchangeScreen.kt`
* **What is generic today:**
  * The 2×4 offer grid cards all use standard `osrsPanel()` with plain text `"Slot N — Empty"` placeholders and a flat `OsrsProgressBar`.
  * Price Check search results and detail view sit in plain `osrsPanel` / `osrsSlot` boxes.
* **Where flair goes:**
  * **Active Offer Cards:** Medium bracketed ledger frame (`FrameWeight.MEDIUM`) with a recessed brass header rule, stamped **Sealing Wax / Brass status badge** (`BUY` / `SELL` / `DONE` / `CANCELLED`), and an engraved progress trough with tick notches at 25/50/75%.
  * **Empty Offer Slots:** Recessed stone tablet with a faint engraved coin-scale watermark (`Ornaments.drawWatermarkScale`) so empty slots look like unused ledger berths rather than blank boxes.
  * **Price Check Panel:** Framed as an **Assayer's Parchment Ledger** header plaque over clean recessed result rows.

### `ProgressionScreen.kt` (Skills 3×8 Grid & Quests Tab)
* **What is generic today:**
  * Skills 3×8 grid cells are plain `osrsSlot()` boxes with a thin bottom progress strip; Combat Level and Total Level summary cards are plain `osrsPanel()` boxes.
  * Quest summary counters (`Completed`, `In Progress`, `Not Started`, `QP`) are plain slots; quest rows sit inside a single `osrsPanel()` separated by `1.dp` lines; empty quest filter state is plain text.
* **Where flair goes:**
  * **Combat & Total Level Banner:** Heavy plaque (`FrameWeight.HEAVY`) with gold-leaf numerals and chiseled corner flourishes.
  * **Skills 3×8 Grid:** Outer container framed in heavy carved stone with iron corner brackets; individual skill cells keep high readability but gain a subtle chiseled top-edge bevel, a gold-leaf border glint for level 99s (`isMaxed`), and a segmented brass XP bar at the base.
  * **Quests Tab:** Filter chips styled as stamped metal tags (`OsrsFilterChip`); quest list header rendered as an engraved stone plaque; **Rolled Parchment Scroll** vignette (`EmptyStates.QuestScroll`) when no quests match or none are synced.

### `TimersScreen.kt` (Dailies, Birdhouses, Patches, Payments) & `EditableList.kt`
* **What is generic today:**
  * All 4 tabs render rows inside a single `osrsPanel` separated by `OsrsHeaderDivider()`, with no table column header plaque, no alternating row tint, and status shown only by text color (`Ready`, `Growing (42m)`, `Empty`).
  * `EditableList.kt` (triggered by the top-bar Edit icon on Dailies/Patches/Payments) uses default Material3 `Checkbox` and a raw `"≡"` text character for drag handles.
* **Where flair goes:**
  * **Timers Table System (`Tables.kt`):** Keeps the exact table layout and columns on all 4 tabs while dressing them as an **Engraved Stone & Parchment Record**:
    * **Table Header Plaque (`OsrsTableHeader`):** Chamfered dark-oak/stone header bar with iron end-studs and small-caps column labels (`TASK / PATCH`, `LOCATION / PAYMENT`, `STATUS`).
    * **Alternating Ledger Rows (`OsrsTableRow`):** Even rows on `OsrsPanelDark`, odd rows tinted with a subtle warm parchment wash (`OsrsLedgerRowAlt`, 4% contrast delta so text stays > 7:1), separated by `1.dp` chiseled dual-line grooves (`OsrsEngravedRowRule`).
    * **Status Cell (`OsrsTableStatusCell`):** Always displays both a drawn status glyph (`Ready` check-seal, `Growing` hourglass/sprout, `Empty` hollow diamond, `Protected` shield-tick) **and** the plain word (`Ready`, `Growing (12m)`, `Empty`, `Protected`) inside a subtle pill border.
  * **Empty States:** Code-drawn vignettes for **Fallow Patch** (`EmptyStates.FallowPatch`), **Empty Birdhouse** (`EmptyStates.Birdhouse`), and **Unmarked Sundial** (`EmptyStates.Sundial`) with existing second-person copy.
  * **`EditableList.kt`:** Replaces Material3 `Checkbox` with `OsrsCheckbox` and replaces `"≡"` with a code-drawn **Wrought-Iron Grip Handle** (`Ornaments.IronGripHandle`).

### `SetupScreen.kt`
* **What is generic today:**
  * A centered column on flat `OsrsDarkStone` with plain numbered `osrsPanel` step cards (`1`, `2`, `3`) and a plain `"Go to dashboard"` button.
* **Where flair goes:**
  * Full-bleed **Carved Stone Ground** with an ornate **Title Ledger Plaque** at the top (`"Pssst"` in gold-leaf with chiseled divider flourish), step numbers stamped on **Crimson Wax / Brass medallions** (`Plaques.StepMedallion`), and a heavy iron-strapped primary action button.

---

## 2. Material Library (6 Procedural Materials)

All materials use existing tokens in `app/src/main/java/com/pssst/theme/Color.kt` plus derived shades added to `Color.kt` with explicit purpose comments. Every procedural texture is generated once per seed/tile size into a small `64×64` or `128×128` `ImageBitmap` tiled via `ShaderBrush(ImageShader(..., TileMode.Repeated, TileMode.Repeated))` and remembered/cached in `Textures.kt` (`minSdk 24` safe, zero per-frame allocations).

| # | Material | Existing Tokens (`Color.kt`) | New Derived Tokens (`Color.kt`) | Code Generation (`Textures.kt`) |
|---|---|---|---|---|
| 1 | **Carved Stone** (`Modifier.stoneGround`, `Modifier.carvedStonePanel`) | `OsrsDarkStone` (`#191612`), `OsrsPanelDark` (`#2B251D`), `OsrsPanelLight` (`#3B3327`), `OsrsBorderLight` (`#5D503E`), `OsrsBorderDark` (`#120E0A`) | `OsrsStonePoreDark = Color(0xFF14110D)` (deep stone pit), `OsrsStoneFlecks = Color(0xFF473E31)` (warm mineral fleck) | 2D value-noise + deterministic FNV-1a hash speckle cached in a `96×96` repeatable `ImageBitmap` (`alpha ≤ 0.08f`), overlaid with a subtle top-lit vertical gradient and `2.dp` chiseled bevel edges. |
| 2 | **Aged Dark Oak** (`Modifier.oakBeam`) | `OsrsPanelDark` (`#2B251D`), `OsrsBorderDark` (`#120E0A`), `OsrsOuterFrame` (`#0C0906`) | `OsrsOakBase = Color(0xFF241B13)` (heartwood plank), `OsrsOakGrainDark = Color(0xFF16100B)` (growth ring trough), `OsrsOakGrainLight = Color(0xFF36281C)` (planed ridge) | Anisotropic horizontal sine-perturbed value noise (`stretchX = 8f, stretchY = 1f`) in a `128×64` tiled `ImageBitmap` + plank seam line (`1.dp` dark groove + `1.dp` highlight) for the top bar and table headers. |
| 3 | **Weathered Parchment** (`Modifier.parchmentSurface`, `Modifier.parchmentRowTint`) | `OsrsTextPrimary` (`#FFE0A0`), `OsrsTextMuted` (`#B8A88A`), `OsrsSlotRecessed` (`#352E24`) | `OsrsParchmentDark = Color(0xFF332B20)` (darkened vellum ground for dark UI), `OsrsParchmentWarm = Color(0xFF3F3527)` (aged paper highlight), `OsrsParchmentStain = Color(0xFF261F16)` (edge foxing stain), `OsrsLedgerRowAlt = Color(0xFF312A20)` (odd table row wash) | Dark-mode aged vellum (keeps dark-theme legibility instead of blinding white paper): cross-hatched linen fiber noise (`64×64` tiled bitmap) + radial edge vignette (`Brush.radialGradient`) so `OsrsTextPrimary` (`#FFE0A0`) maintains **9.2:1** contrast and `OsrsTextMuted` (`#B8A88A`) maintains **5.1:1** contrast. |
| 4 | **Worn Binding Leather** (`Modifier.leatherPanel`) | `OsrsPanelDark` (`#2B251D`), `OsrsBorderOutline` (`#453B2F`) | `OsrsLeatherBase = Color(0xFF281C15)` (oxblood-brown hide), `OsrsLeatherCrease = Color(0xFF18100C)` (pebble grain trough), `OsrsLeatherHighlight = Color(0xFF3A291F)` (worn crown), `OsrsLeatherStitch = Color(0xFF7A6548)` (waxed thread dash) | Voronoi/cellular pebble-grain noise cached in a `64×64` tiled `ImageBitmap` + inset dashed saddle-stitching (`PathEffect.dashPathEffect`) `4.dp` inside the border for the settings drawer, inventory pack tray, and bookmark tabs. |
| 5 | **Wrought Iron & Brass Hardware** (`Frames.kt`, `Plaques.kt`) | `OsrsRivetLight` (`#8A7960`), `OsrsRivetDark` (`#1F1A14`), `OsrsGoldHighlight` (`#FFCA7A`), `OsrsGold` (`#FF981F`), `OsrsGoldDark` (`#D6780E`), `OsrsGoldShadow` (`#8C4800`) | `OsrsIronBody = Color(0xFF23201D)` (forged strap fill), `OsrsIronEdgeLight = Color(0xFF524B42)` (hammered iron glint), `OsrsIronEdgeDark = Color(0xFF0E0C0A)` (iron shadow), `OsrsBrassMid = Color(0xFFB8863B)` (aged brass bezel) | Drawn directly in `DrawScope` via `drawWithCache`: hammered iron corner L-brackets, T-hinge straps, beveled dome rivets (`drawCircle` + crescent highlight arc), and 3-stop metallic gold-leaf vertical gradients (`OsrsGoldHighlight` → `OsrsGold` → `OsrsGoldDark`) with hard `1.px` `OsrsOuterFrame` shadow. |
| 6 | **Crimson Sealing Wax** (`Plaques.kt`) | `OsrsRed` (`#FF4D4D`), `OsrsOuterFrame` (`#0C0906`) | `OsrsWaxCrimson = Color(0xFF7A1C1C)` (deep melted wax body), `OsrsWaxHighlight = Color(0xFFB83232)` (wax rim ridge), `OsrsWaxShadow = Color(0xFF3D0B0B)` (stamped recess) | Vector/Canvas 12-lobe scalloped wax seal (`Path` with polar wave radius) + recessed inner ring and stamped numeral/glyph for setup step medallions, danger-zone badges, and sync status seals. |

---

## 3. Motif Vocabulary (One Cohesive Family)

Every ornament shares the same **45° chamfered chisel geometry** and **`1.5.dp` forged-iron stroke weight**:

1. **Corner Hardware (3 Weights in `Frames.kt`):**
   * **`FrameWeight.LIGHT` (Studs):** Single `3.dp` beveled iron rivet in each corner (`4.dp` inset) with top-left specular crescent and bottom-right shadow. Used on small cards and dialogs.
   * **`FrameWeight.MEDIUM` (L-Brackets):** `12.dp × 12.dp` chamfered wrought-iron corner L-plates hugging the frame border with two `2.5.dp` rivets per bracket. Used on standard dashboard cards, GE active offers, and Equipment panel.
   * **`FrameWeight.HEAVY` (Hinge Straps):** Forged horizontal iron strap plates extending `20.dp` inward from each corner with a notched spearhead tip and triple rivets, plus a `45°` chamfered inner gold/stone fillet. Used on Top Bar, Total Wealth card, Goal card, Skills summary banner, and Timers table header.
2. **Plaques & Headers (`Plaques.kt`):**
   * **Chamfered Stone Plaque (`OsrsTitlePlaque` / `OsrsSectionPlaque`):** Octagonal-chamfered (`4.dp` cut corners) recessed stone or oak plate, framed in `OsrsBrassMid` / `OsrsBorderLight` with two side rivets.
   * **Gold-Leaf Lettering (`OsrsGoldLeafText`):** Renders display/header text twice or via `TextStyle(brush = GoldLeafBrush, shadow = Shadow(color = OsrsOuterFrame, offset = Offset(2f, 2f), blurRadius = 0f))` — a crisp `1.dp` hard drop shadow with zero blur.
   * **Physical Bookmark Tabs (`OsrsSegmentedTabs`):** Active tab rises `2.dp` higher than inactive tabs, wears a warm leather/parchment top surface with stitched side borders and a brass top clip; inactive tabs sit recessed in dark stone.
3. **Dividers & Ornaments (`Ornaments.kt`):**
   * **Engraved Rule (`OsrsHeaderDivider`):** `1.dp` dark chisel groove (`OsrsBorderDark`) over `1.dp` bottom highlight (`OsrsBorderLight`), terminated at both ends by **45° diamond chisel caps** and centered with a small **forged diamond-and-chevron flourish**.
   * **Chiseled Bullet Glyph (`OsrsBulletGlyph`):** `6.dp` faceted diamond in `OsrsGoldDark` / `OsrsBrassMid` replacing raw text `"•"` characters.
   * **Iron Grip Handle (`OsrsIronGripHandle`):** Three recessed horizontal iron bars with side rivets replacing raw `"≡"` in `EditableList.kt`.
4. **Unified Icon & Badge Hand (`Icons.kt`):**
   * All bottom-nav, top-bar, drawer, and status badges built as `ImageVector`s on a `24.dp × 24.dp` grid, `1.75.dp` stroke, miter/bevel joins, 45° chamfered corners, dual-tone fill (`currentColor` at `0.18f` alpha inside the stroke):
     * **Nav:** `OverviewShield` (kite shield with chevron boss), `StorageChest` (iron-strapped arched coffer), `ExchangeCoins` (stacked stamped GP coins + scale beam), `ProgressStar` (8-point compass rose star), `TimersHourglass` (wood-and-brass twin-bulb sandglass).
     * **Status Badges:** `BadgeReady` (wax/brass seal with chiseled check), `BadgeGrowing` (sprouting hourglass), `BadgeEmpty` (recessed hollow diamond), `BadgeWarning` (chamfered iron triangle with exclamation).
5. **Empty-State Vignettes (`EmptyStates.kt`):**
   * `56.dp × 56.dp` code-drawn two-tone illustrations (`OsrsBorderLight`, `OsrsGoldDark`, `OsrsIronBody`) framed inside a chamfered diamond medallion above the existing second-person copy:
     * `ClosedChest` (Bank empty / Bank sharing off)
     * `PotionRack` (Potion storage empty)
     * `QuestScroll` (Quests empty)
     * `FallowPatch` (Farming patches empty)
     * `Birdhouse` (Birdhouses empty)
     * `Sundial` (Dailies empty)
     * `LedgerBookmark` (Goals empty)

---

## 4. Component & Screen Mapping

| Target File | Component / Area | Material | Frame Weight & Motifs |
|---|---|---|---|
| `Theme.kt` | `Modifier.osrsPanel` | Carved Stone (`Textures.carvedStone`) | Default `FrameWeight.MEDIUM` (upgradeable parameter `weight: FrameWeight = FrameWeight.MEDIUM`), chiseled bevel + iron corner hardware |
| `Theme.kt` | `Modifier.osrsSlot` | Recessed Stone (`OsrsSlotRecessed`) | `45°` 2px chamfered inner shadow well (`OsrsBorderDark` top/left, `OsrsBorderLight` bottom/right) |
| `Theme.kt` | `OsrsHeaderDivider` | Engraved Stone Groove | Dual-line chiseled groove + diamond end-caps + optional center chevron |
| `Theme.kt` | `OsrsProgressBar` | Recessed Iron Trough | Dark iron channel, 25/50/75% tick notches, bevel-highlighted fill bar |
| `Theme.kt` | `OsrsButton` | Forged Iron / Brass Plaque | Primary = brass-trimmed oak/stone plaque with corner studs; Secondary = wrought-iron frame |
| `Theme.kt` | `OsrsStatusBadge` | Stamped Brass / Wax Tag | Chamfered tag corners + `1.dp` inset rim + optional status glyph |
| `Theme.kt` | `OsrsFilterChip` | Stamped Metal Tag | Chamfered corners, side rivet dot when selected, gold-leaf active text |
| `Theme.kt` | `OsrsSegmentedTabs` | Worn Leather + Brass | Physical bookmark tabs sitting in a recessed iron rail |
| `Theme.kt` | `OsrsSwitch` / `OsrsCheckbox` | Wrought Iron + Gold | Iron track with riveted brass thumb; chiseled stone checkbox well with forged check mark |
| `Theme.kt` | `OsrsTextField` | Recessed Parchment Well | Dark vellum interior (`OsrsParchmentDark`) with iron corner ticks |
| `DashboardScreen.kt` | Top Bar & Bottom Nav | Aged Dark Oak (top), Carved Stone (bottom), Worn Leather (drawer) | Top bar = `FrameWeight.HEAVY` oak beam + iron straps; Bottom nav = `Icons.kt` vectors + brass active well; Drawer = leather grain + `OsrsSectionPlaque` headers |
| `ReadyStrip.kt` | 4 Top-Bar Status Slots | Recessed Stone + Brass Rim | `Icons.kt` glyphs + warm gold rim glow when `ready > 0` |
| `OverviewScreen.kt` | Wealth, Goals, Slayer, Miscellania, Quick Actions, Phone Address | Carved Stone, Weathered Parchment (`QuickActionsBanner`, `PhoneAddressCard` URL slip), Sealing Wax | `TotalWealthCard` & `GoalCard` = `FrameWeight.HEAVY`; others = `FrameWeight.MEDIUM`; `QuickActionsBanner` = pinned parchment strip; `GoalCard` empty = `LedgerBookmark` vignette |
| `StorageScreen.kt` + sub-screens | Bank, Inventory, Potions | Carved Stone (Bank/Equipment), Worn Leather (Inventory pack), Brass/Glass (Potions) | Bookmark segmented tabs; `ClosedChest` & `PotionRack` empty states; notched 4-dose vial bar |
| `GrandExchangeScreen.kt` | 8 Offer Tiles + Price Check | Carved Stone + Parchment Header | Active tiles = `FrameWeight.MEDIUM`; Empty tiles = recessed tablet with `drawWatermarkScale`; notched progress bars |
| `ProgressionScreen.kt` | Skills 3×8 Grid + Quests | Carved Stone + Gold Leaf | Level summary = `FrameWeight.HEAVY`; 99 skills = brass/gold corner glint; Quests empty = `QuestScroll` vignette |
| `TimersScreen.kt` + `EditableList.kt` | Dailies, Birdhouses, Patches, Payments | Aged Dark Oak (table header), Carved Stone + `OsrsLedgerRowAlt` (alternating rows) | `Tables.kt`: `OsrsTableHeader` plaque, `OsrsTableRow` alternating tint + engraved groove, `OsrsTableStatusCell` (icon + word), `Sundial`/`Birdhouse`/`FallowPatch` empty states, `IronGripHandle` in edit mode |
| `SetupScreen.kt` | Onboarding Ledger | Carved Stone Ground + Crimson Wax | Ornate title plaque, `StepMedallion` wax seals (`1`, `2`, `3`), heavy primary button |

---

## 5. Restraint Rules (Where NOT to Decorate)

1. **Dense Data Cells Stay Clean:**
   * Bank item grid cells (`BankScreen`), Inventory 28-slot cells (`InventoryScreen`), Equipment 11-slot cells, Skill 3×8 cells (`ProgressionScreen`), and inner text of Timers table cells (`TimersScreen`) **never** receive heavy corner brackets, busy noise, or decorative flourishes inside the cell. They remain crisp recessed stone wells (`osrsSlot`) or low-contrast alternating rows (`alpha ≤ 0.05f` texture) so numbers and item sprites read immediately.
2. **Strict Contrast Floor (≥ 4.5:1):**
   * All body and numeric text uses `OsrsTextPrimary` (`#FFE0A0`), `OsrsWhite` (`#FFFFFF`), `OsrsGold` (`#FF981F`), `OsrsGreen` (`#00FF80`), or `OsrsTextMuted` (`#B8A88A`) against dark grounds (`#191612` to `#352E24`).
   * Minimum measured pair: `OsrsTextMuted` (`#B8A88A`) on `OsrsSlotRecessed` (`#352E24`) = **4.82:1**; `OsrsTextMuted` on `OsrsLedgerRowAlt` (`#312A20`) = **5.14:1**; `OsrsTextPrimary` (`#FFE0A0`) on `OsrsParchmentDark` (`#332B20`) = **9.20:1**.
   * Procedural noise overlays behind text are capped at `alpha = 0.06f` so local pixel contrast never drops below `4.5:1`.
3. **Zero Frame-Time Allocations:**
   * All procedural `ImageBitmap` tiles (`64×64` to `128×128`) are generated once and cached in top-level `lazy` / `remember` holders keyed by seed.
   * All frame hardware, bevels, rules, and plaques draw inside `Modifier.drawWithCache` so `Path` and `Brush` instances are only built when component bounds change.
4. **Originality & Copy Integrity:**
   * Zero Jagex/OSRS/RuneLite fonts, logos, skill icons, side-panel stones, minimap orbs, or chatbox frames.
   * All existing screen layouts, table structures, setting labels, and second-person factual copy remain unchanged.
