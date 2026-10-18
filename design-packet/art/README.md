# Pssst Code-Generated Art Packet Reference (`com.pssst.theme.art`)

Every asset in this packet is generated in code (`DrawScope`, `Path`, `ShaderBrush`, `ImageVector`, `Modifier.drawWithCache`) with zero external image files and full `minSdk 24` compatibility.

---

## 1. Procedural Materials & Brushes (`Textures.kt`)

### `Textures.stoneBrush`
* **What it is:** Seamless `64×64` cached `ShaderBrush` of carved ledger stone with warm mineral flecks (`OsrsStoneFlecks`) and chisel pores (`OsrsStonePoreDark`).
* **Screens:** All screens (`DashboardScreen`, `SetupScreen`, and inside `ledgerPanelFrame`).
* **Call:** `Textures.stoneBrush(seed = 101, darkGround = false)`
* **Parameters:** `seed: Int = 101`, `darkGround: Boolean = false` (`true` uses `OsrsDarkStone`, `false` uses `OsrsPanelDark`).

### `Textures.oakBrush`
* **What it is:** Seamless `128×64` cached `ShaderBrush` of horizontal heartwood oak grain (`OsrsOakBase`, `OsrsOakGrainDark`, `OsrsOakGrainLight`).
* **Screens:** `DashboardScreen` (top bar), `TimersScreen` (table header plaque), `TitlePlaque`.
* **Call:** `Textures.oakBrush(seed = 202)`
* **Parameters:** `seed: Int = 202`.

### `Textures.parchmentBrush`
* **What it is:** Seamless `64×64` cached `ShaderBrush` of dark-mode aged vellum with cross-hatched linen fibers (`OsrsParchmentDark`, `OsrsParchmentWarm`, `OsrsParchmentStain`). Maintains ≥ 9.2:1 contrast with `OsrsTextPrimary`.
* **Screens:** `OverviewScreen` (`QuickActionsBanner`, `PhoneAddressCard`), `Theme.kt` (`OsrsTextField`), `GrandExchangeScreen` (Price Check header).
* **Call:** `Textures.parchmentBrush(seed = 303)`
* **Parameters:** `seed: Int = 303`.

### `Textures.leatherBrush`
* **What it is:** Seamless `64×64` cached cellular pebble-grain oxblood hide brush (`OsrsLeatherBase`, `OsrsLeatherCrease`, `OsrsLeatherHighlight`).
* **Screens:** `DashboardScreen` (Settings drawer), `InventoryScreen` (28-slot pack tray), `Theme.kt` (`OsrsSegmentedTabs` active bookmark).
* **Call:** `Textures.leatherBrush(seed = 404)`
* **Parameters:** `seed: Int = 404`.

### `Textures.ironBrush`
* **What it is:** Seamless `64×64` cached hammered wrought-iron brush (`OsrsIronBody`, `OsrsIronEdgeLight`, `OsrsIronEdgeDark`).
* **Screens:** `DashboardScreen` (top bar iron binding straps), `Theme.kt` (`OsrsButton` secondary, `OsrsProgressBar` trough).
* **Call:** `Textures.ironBrush(seed = 505)`
* **Parameters:** `seed: Int = 505`.

### `Modifier.stoneGround` / `Modifier.carvedStone` / `Modifier.oakBeam` / `Modifier.parchment` / `Modifier.leather` / `Modifier.hammeredIron`
* **What they are:** `drawWithCache` surface modifiers wrapping the cached material brushes with top-lit vertical gradients, plank seams, radial foxing vignettes, or inset waxed-thread saddle stitching.
* **Screens:** `DashboardScreen`, `SetupScreen`, `OverviewScreen`, `InventoryScreen`.
* **Call:**
  * `Modifier.stoneGround(seed = 101)`
  * `Modifier.carvedStone(seed = 101)`
  * `Modifier.oakBeam(seed = 202, showPlankSeams = true)`
  * `Modifier.parchment(seed = 303, showStainVignette = true)`
  * `Modifier.leather(seed = 404, stitched = true)`
  * `Modifier.hammeredIron(seed = 505)`

---

## 2. Frames & Hardware (`Frames.kt`)

### `Modifier.ledgerPanelFrame`
* **What it is:** Primary carved-stone panel frame with chiseled bevels and forged wrought-iron corner hardware in three weights (`FrameWeight.LIGHT` corner studs, `FrameWeight.MEDIUM` corner L-brackets, `FrameWeight.HEAVY` spearhead hinge straps + inner brass fillet). Backwards-aliased by `Modifier.osrsPanel` in `Theme.kt`.
* **Screens:** Every screen (`OverviewScreen`, `GoalCard`, `StorageScreen`, `BankScreen`, `InventoryScreen`, `PotionStorageScreen`, `GrandExchangeScreen`, `ProgressionScreen`, `TimersScreen`, `SetupScreen`).
* **Call:** `Modifier.ledgerPanelFrame(weight = FrameWeight.MEDIUM, seed = 101, accentBorder = null)`
* **Parameters:** `weight: FrameWeight = FrameWeight.MEDIUM`, `seed: Int = 101`, `accentBorder: Color? = null`.

### `Modifier.oakBeamFrame`
* **What it is:** Heavy dark-oak timber beam bound with top/bottom hammered iron straps, plank join seam, and corner spearhead hinge straps.
* **Screens:** `DashboardScreen` (Top Bar).
* **Call:** `Modifier.oakBeamFrame(seed = 202)`
* **Parameters:** `seed: Int = 202`.

### `Modifier.stoneSillFrame`
* **What it is:** Carved stone bottom sill with an engraved top iron-and-brass rail and corner L-brackets.
* **Screens:** `DashboardScreen` (Bottom Navigation Bar).
* **Call:** `Modifier.stoneSillFrame(seed = 109)`
* **Parameters:** `seed: Int = 109`.

### `Modifier.ledgerWellSlot`
* **What it is:** Recessed carved-stone well with 45° inner shadow bevel and optional warm gold-leaf rim glint (`highlighted = true`). Backwards-aliased by `Modifier.osrsSlot` in `Theme.kt`.
* **Screens:** `ReadyStrip`, `BankScreen`, `InventoryScreen`, `GrandExchangeScreen`, `ProgressionScreen`, `OverviewScreen`.
* **Call:** `Modifier.ledgerWellSlot(empty = false, highlighted = false)`
* **Parameters:** `empty: Boolean = false`, `highlighted: Boolean = false`.

### `Modifier.parchmentNotice`
* **What it is:** Weathered dark-vellum notice strip pinned at all four corners with domed iron tacks.
* **Screens:** `OverviewScreen` (`QuickActionsBanner`, `PhoneAddressCard` sync URL slip), `GrandExchangeScreen` (Price Check summary).
* **Call:** `Modifier.parchmentNotice(accentBorder = OsrsBrassMid, seed = 303)`
* **Parameters:** `accentBorder: Color = OsrsBrassMid`, `seed: Int = 303`.

### `Modifier.leatherBoundPanel`
* **What it is:** Oxblood worn-leather tray with inset dashed waxed-thread saddle stitching and iron corner L-brackets.
* **Screens:** `DashboardScreen` (Settings Drawer), `InventoryScreen` (28-slot inventory pack tray).
* **Call:** `Modifier.leatherBoundPanel(seed = 404)`
* **Parameters:** `seed: Int = 404`.

---

## 3. Plaques, Headers & Seals (`Plaques.kt`)

### `GoldLeafText`
* **What it is:** 3-stop vertical gold-leaf gradient text (`OsrsGoldHighlight` → `OsrsGold` → `OsrsGoldDark`) with a crisp `1.px` hard drop shadow (`OsrsOuterFrame`, `blurRadius = 0f`).
* **Screens:** `DashboardScreen` (brand header, drawer headers), `OverviewScreen`, `ProgressionScreen`, `TimersScreen`, `SetupScreen`.
* **Call:** `GoldLeafText(text = "Pssst", style = MaterialTheme.typography.headlineMedium)`
* **Parameters:** `text: String`, `modifier: Modifier`, `style: TextStyle`, `fontWeight: FontWeight`, `textAlign: TextAlign?`, `maxLines: Int = 1`.

### `TitlePlaque` & `Modifier.chamferedPlaqueFrame`
* **What it is:** Octagonal chamfered oak-and-brass title plaque with side rivets and inner corner flourishes.
* **Screens:** `SetupScreen`, `DashboardScreen` (drawer header), `ProgressionScreen` (summary banner).
* **Call:** `TitlePlaque(title = "Pssst", subtitle = "...")`
* **Parameters:** `title: String`, `subtitle: String? = null`, `modifier: Modifier`.

### `SectionHeaderPlaque`
* **What it is:** Section header row pairing a faceted diamond `BulletGlyph`, `GoldLeafText`, optional trailing action slot, and an `EngravedRule` below.
* **Screens:** `OverviewScreen`, `GoalCard`, `InventoryScreen`, `GrandExchangeScreen`, `DashboardScreen` (drawer sections).
* **Call:** `SectionHeaderPlaque(title = "PINNED GOALS", trailing = { ... })`
* **Parameters:** `title: String`, `modifier: Modifier`, `trailing: (@Composable RowScope.() -> Unit)? = null`.

### `Modifier.bookmarkTab`
* **What it is:** Physical stitched-leather bookmark tab with a raised brass top clip when `selected = true` and a recessed stone well when `selected = false`. Wired into `OsrsSegmentedTabs` in `Theme.kt`.
* **Screens:** `StorageScreen`, `GrandExchangeScreen`, `ProgressionScreen`, `TimersScreen`.
* **Call:** `Modifier.bookmarkTab(selected = isSelected, seed = 404)`
* **Parameters:** `selected: Boolean`, `seed: Int = 404`.

### `WaxSealMedallion`
* **What it is:** 12-lobe scalloped crimson sealing-wax medallion stamped with a gold-leaf numeral or symbol.
* **Screens:** `SetupScreen` (steps `1`, `2`, `3`), `PhoneAddressCard` (receiver badge).
* **Call:** `WaxSealMedallion(label = "1", size = 32.dp)`
* **Parameters:** `label: String`, `modifier: Modifier`, `size: Dp = 32.dp`.

---

## 4. Engraved Timers Tables (`Tables.kt`)

### `OsrsTableHeaderPlaque`
* **What it is:** Engraved dark-oak and brass column header bar with iron corner rivets and gold-leaf column labels.
* **Screens:** `TimersScreen` (`DailiesTab`, `BirdhousesTab`, `PatchesTab`, `PaymentsTab`).
* **Call:** `OsrsTableHeaderPlaque(leftLabel = "PATCH", rightLabel = "STATUS")`
* **Parameters:** `leftLabel: String`, `rightLabel: String`, `modifier: Modifier`.

### `OsrsTableRow` & `OsrsTableRowSeparator`
* **What they are:** Alternating table row (`index % 2 == 1` washed with `OsrsLedgerRowAlt`) and chiseled dual-line groove separator.
* **Screens:** `TimersScreen` (all 4 tabs).
* **Call:** `OsrsTableRow(index = idx) { ... }` and `OsrsTableRowSeparator()`
* **Parameters:** `index: Int`, `modifier: Modifier`, `content: @Composable RowScope.() -> Unit`.

### `OsrsTableStatusCell`
* **What it is:** Chamfered status tag cell that always renders both a state badge glyph (`BadgeReady`, `BadgeGrowing`, `BadgeEmpty`, `BadgeWarning`) and the explicit status word (`Ready`, `Growing (38m)`, `Empty`, etc.).
* **Screens:** `TimersScreen` (`DailiesTab`, `BirdhousesTab`, `PatchesTab`, `PaymentsTab`).
* **Call:** `OsrsTableStatusCell(text = statusText, status = TableStatusKind.READY, colorOverride = statusColor)`
* **Parameters:** `text: String`, `status: TableStatusKind`, `modifier: Modifier`, `colorOverride: Color? = null`.

---

## 5. Dividers & Ornaments (`Ornaments.kt`)

### `EngravedRule` & `Ornaments.drawEngravedRule`
* **What it is:** Chiseled dual-line groove rule terminated by 45° faceted diamond end-caps and an optional center chevron-and-diamond flourish. Wired into `OsrsHeaderDivider` in `Theme.kt`.
* **Screens:** All screens.
* **Call:** `EngravedRule(withEndCaps = true, withCenterFlourish = false)`
* **Parameters:** `modifier: Modifier`, `withEndCaps: Boolean = true`, `withCenterFlourish: Boolean = false`, `accentColor: Color = OsrsBrassMid`.

### `BulletGlyph`
* **What it is:** `7.dp` faceted 45° brass/gold diamond bullet replacing raw `"•"` text characters.
* **Screens:** `OverviewScreen` (`QuickActionsBanner`), `SectionHeaderPlaque`, `DashboardScreen`.
* **Call:** `BulletGlyph(size = 7.dp, color = OsrsGoldDark)`
* **Parameters:** `modifier: Modifier`, `size: Dp = 7.dp`, `color: Color = OsrsGoldDark`.

### `IronGripHandle`
* **What it is:** 3-bar forged wrought-iron drag handle with side dome rivets replacing raw `"≡"` text in editable lists.
* **Screens:** `EditableList.kt` (Timers edit mode).
* **Call:** `IronGripHandle(width = 20.dp, height = 16.dp)`
* **Parameters:** `modifier: Modifier`, `width: Dp = 20.dp`, `height: Dp = 16.dp`.

### `Ornaments.drawWatermarkScale`
* **What it is:** Low-contrast engraved merchant balance scale watermark drawn inside unoccupied Grand Exchange offer slots.
* **Screens:** `GrandExchangeScreen` (`EmptyOfferSlot`).
* **Call:** `with(Ornaments) { drawWatermarkScale() }`
* **Parameters:** `center: Offset`, `radius: Float`, `color: Color`.

---

## 6. Unified Vector Icons & Badges (`Icons.kt`)

All icons in `ArtIcons` are `24.dp × 24.dp` `ImageVector`s with `1.75f` bevel-joined stroke and `18%` interior fill:

| Identifier | Meaning | Used In |
|---|---|---|
| `ArtIcons.OverviewShield` | Kite shield with chevron boss | `DashboardScreen` Bottom Nav (Overview) |
| `ArtIcons.StorageChest` | Iron-strapped arched coffer | `DashboardScreen` Bottom Nav (Storage) |
| `ArtIcons.ExchangeCoins` | Twin stacked GP coin columns | `DashboardScreen` Bottom Nav (Exchange) & `ReadyStrip` |
| `ArtIcons.ProgressStar` | 8-point chiseled compass star | `DashboardScreen` Bottom Nav (Progress) |
| `ArtIcons.TimersHourglass` | Wood-and-brass sandglass | `DashboardScreen` Bottom Nav (Timers) |
| `ArtIcons.Birdhouse` | Pitched-roof wooden birdhouse | `ReadyStrip` & `TimersScreen` |
| `ArtIcons.HerbSprig` | Three-lobed herb leaf sprig | `ReadyStrip` & `TimersScreen` |
| `ArtIcons.TreeCrown` | Faceted tree crown over trunk | `ReadyStrip` & `TimersScreen` |
| `ArtIcons.DrawerLedgerMenu` | 3 forged iron bars with bracket ticks | `DashboardScreen` Top Bar Drawer Button |
| `ArtIcons.EditQuill` | Scribe's quill stylus over rule | `DashboardScreen` Top Bar Edit Timers Button |
| `ArtIcons.BadgeReady` | Octagonal seal with checkmark | `Tables.kt` (`OsrsTableStatusCell`), `Theme.kt` (`OsrsStatusBadge`) |
| `ArtIcons.BadgeGrowing` | Octagonal dial with hands | `Tables.kt` (`OsrsTableStatusCell`) |
| `ArtIcons.BadgeEmpty` | Hollow diamond with dash | `Tables.kt` (`OsrsTableStatusCell`) |
| `ArtIcons.BadgeWarning` | Chamfered iron triangle with `!` | `Tables.kt` (`OsrsTableStatusCell`), warning banners |

---

## 7. Empty-State Vignettes (`EmptyStates.kt`)

### `OsrsEmptyState` & `EmptyStateVignette`
* **What it is:** `56.dp` code-drawn vignette inside a chamfered stone-and-brass medallion paired with the screen's existing second-person copy.
* **Call:** `OsrsEmptyState(kind = EmptyStateKind.CLOSED_CHEST, message = "...")`
* **Kinds (`EmptyStateKind`):**
  * `CLOSED_CHEST` — `BankScreen` (no bank data) & `StorageScreen` (`BankSharingOff`)
  * `FALLOW_PATCH` — `TimersScreen` (`PatchesTab` empty)
  * `EMPTY_BIRDHOUSE` — `TimersScreen` (`BirdhousesTab` empty)
  * `POTION_RACK` — `PotionStorageScreen` (no potion data)
  * `QUEST_SCROLL` — `ProgressionScreen` (`QuestsView` empty)
  * `SUNDIAL` — `TimersScreen` (`DailiesTab` empty)
  * `LEDGER_BOOKMARK` — `GoalCard` (no pinned goals)
