# Pssst Pixel-Art Overhaul — Gemini + Claude Art Duet Log

- **Project**: `C:\Users\Fugma\projects\Pssst---OSRS-Companion-App--Android`
- **Branch**: `feat/piskel-art-duet`
- **Lead**: `★ LEAD · PSSST ART ord1` (`95adc099-69fe-4422-9d02-e5833ba99a8c`)

---

## Step 1 — PLAN (Gemini `antigravity-cli/gemini-4-argon`)
- **Status**: COMPLETED
- **Artifact**: [PSSST_ART_PLAN.md](file:///C:/Users/Fugma/projects/Pssst---OSRS-Companion-App--Android/docs/art_duet/PSSST_ART_PLAN.md)
- **Summary**:
  - Audited baseline contact sheets (`sprites.png`, `scenes.png`, `frames_plaques_ornaments.png`, `materials.png`) and screen captures (`01_setup.png`, `02_overview.png`, `06_grand_exchange.png`, `14_empty_states.png`) alongside all guardrail tests (`ColourDisciplineTest`, `PixelHonestyTest`, `SpriteGridTest`, `ArtContrastTest`, `MaterialToneTest`, `GuardrailsTest`).
  - Specified `SpriteTones` extension (`'b'`, `'d'`, `'c'` secondary material roles) in `PixelArt.kt` and `SpriteGridTest.kt` to allow multi-material 16×16 OSRS sprites while preserving strict 1px `'o'` outline enclosure.
  - Designed the `36×36` pixel-grid OSRS Informant Crest for `ic_launcher_foreground.xml` (108×108 viewport, 3×3 units per pixel) and `SetupScreen.kt`.
  - Upgraded `EmptyStates.kt` scenes from `32×24` procedural rectangles to `48×36` (exact 4:3 aspect ratio, 4.0× integer scale at `64.dp` `xxhdpi`) character-grid scenes for all 7 empty states (`CLOSED_CHEST`, `FALLOW_PATCH`, `EMPTY_BIRDHOUSE`, `POTION_RACK`, `QUEST_SCROLL`, `SUNDIAL`, `LEDGER_BOOKMARK`).
  - Specified all 16 `ArtIcons` (`16×16`) + `Ornaments.WatermarkScaleSprite` and the 4 Overview vignettes (`KINGDOM_HEALTHY`, `KINGDOM_WARNING`, `SLAYER_ACTIVE`, `SLAYER_COMPLETE` at `32×24`) integrated into `MiscellaniaCard` and `SlayerTaskCard` in `TimersScreen.kt`.


---

## Step 2 — CRITIQUE (Claude Opus `claude/claude-opus-5-5`)
- **Status**: COMPLETED
- **Agent**: `CRITIQUE · PSSST ART (Step 2)` (`a6c28dc9-ed15-48bf-854c-9028af069a99`)
- **Artifact**: [PSSST_ART_CRITIQUE.md](file:///C:/Users/Fugma/projects/Pssst---OSRS-Companion-App--Android/docs/art_duet/PSSST_ART_CRITIQUE.md)
- **Summary**: Identified 7 BLOCKING, 17 SHOULD, and 11 NIT items (`SpriteTones` role collisions with `b`/`d`, call-site retinting of nav/badge sprites, status badge silhouette/CVD distinction, `SetupContent` `TitlePlaque` placement, real `MiscellaniaUiModel` / `SlayerUiModel` field names and threshold alignment, circular safe-zone + monochrome layer + legacy mipmaps for launcher icon, pure draw functions for `ArtSheetExportTest`, `artPx * k` scaling, background contrast floor, `SceneGridTest`, and new phone screen captures for Step 5).

---

## Step 3 — REVISE (Gemini `antigravity-cli/gemini-4-argon`)
- **Status**: COMPLETED
- **Artifact**: [PSSST_ART_PLAN.md](file:///C:/Users/Fugma/projects/Pssst---OSRS-Companion-App--Android/docs/art_duet/PSSST_ART_PLAN.md)
- **Summary**: Resolved all 7 BLOCKING, 17 SHOULD, and 11 NIT items in the revised `PSSST_ART_PLAN.md`. Ready for Step 4 (Claude BUILD).

---

## Step 4 — BUILD (Claude Opus `claude/claude-opus-5-5`)
- **Status**: COMPLETED (uncommitted on `feat/piskel-art-duet`)
- **Pipeline**: Piskel MCP was not connected to the builder session, so art is authored in a committed, deterministic Python kit with the same primitives (`tools/art/`): `pixart.py` (masks, auto 1px outlines, top-left shading, 3x5 font), `crest.py`, `scenes.py` + `scenes_b.py`, `vignettes.py`, `palette.py`, `sprites.py`. `python tools/art/build_art.py` regenerates `ScenePalettes.kt`, `SceneArt.kt`, both launcher vectors and the legacy mipmaps; `python tools/art/sprites.py` writes sprite rows back into `Icons.kt`/`Ornaments.kt`; `python tools/art/preview_sprites.py` lints sprites like `SpriteGridTest`.
- **Delivered**: `SpriteTones` `l/m/k/g` roles with primary-ramp fallback + `QUEST_BLUE` / `TERRACOTTA_SPROUT` / `BLIGHT_WARNING`; `SceneGrid` + `PixelCanvas.drawSceneGrid`; 7 × 48x36 scenes, 36x36 crest, 4 × 32x24 vignettes; 16 new `ArtIcons` + watermark + bullet fix; exact `artPx × k` scaling (`PixelGridArt`); Setup crest; Miscellania/Slayer integration with shared `util/MiscellaniaThresholds`; badges in natural tones; launcher foreground/monochrome/legacy PNGs; `SceneGridTest` (dims, colours, contrast floor, launcher pathData + grid sync), extended `SpriteGridTest`, two-group `scenes.png`, captures `16_empty_vignettes` / `17_overview_vignettes`.
- **Deviations from plan (intentional)**:
  - Slayer gilded = `!hasTask && (points > 0 || streak > 0)`; dropped `hasTask && progress >= 1f` because `progress` returns 1f for active tasks with unknown initial amount.
  - Setup crest renders the 24x24 visible zone of the 36x36 grid at k=3 (72dp), so the crest fills its frame instead of floating in empty margin.
  - Contrast floor = dominant body colour >= 1.8:1 **and** >= 60% of body pixels >= 1.8:1 vs lightest panel stone (strict "top-3 colours" would ban interior shading such as ink lines and furrows).
  - Thresholds live in `com.pssst.util.MiscellaniaThresholds` (util must not depend on theme.art); approval colour threshold moved 75 → 90 as planned.
  - `DrawerLedgerMenu` natural tones stay GOLD (LEATHER is near-invisible on dark stone; the call site is GOLD anyway).
- **Verification**: `./gradlew testDebugUnitTest` 151/151 passing. Sheets: `app/build/art-sheet/{sprites,scenes}.png`; captures `app/build/art-sheet/screens/01_setup.png`, `16_empty_vignettes.png`, `17_overview_vignettes.png`.


---

## Step 5 — SHOT REVIEW (Gemini `antigravity-cli/gemini-4-argon`)
- **Status**: COMPLETED
- **Artifact**: [PSSST_SHOT_REVIEW.md](file:///C:/Users/Fugma/projects/Pssst---OSRS-Companion-App--Android/docs/art_duet/PSSST_SHOT_REVIEW.md)
- **Summary**: Inspected all exported PNG sheets (`sprites.png`, `scenes.png`, `frames_plaques_ornaments.png`, `launcher.png`) and phone screen captures (`01_setup.png`, `02_overview.png`, `11_timers_farming.png`, `14_empty_states.png`, `16_empty_vignettes.png`, `17_overview_vignettes.png`). Documented 8 concrete visual polish items (`SF1`–`SF8`: `InformantCrest` hand/face separation & stone pillar courses, `CLOSED_CHEST` open dark cavity, `EMPTY_BIRDHOUSE` clockwork bird on roof slope, `SUNDIAL` dial plate & gnomon fin, `LEDGER_BOOKMARK` ghost loot silhouette, `KINGDOM_WARNING` gold cracked crown, and `BadgeGrowing` retint role swap) for Step 6 (`FIX & PR`).

---

## Step 6 — FIX & PR (Claude Opus `claude/claude-opus-5-5`)
- **Status**: FIXES APPLIED + COMMITTED on `feat/piskel-art-duet`; push/PR pending owner OK (personal repo).
- **Shot-review fixes** (`PSSST_SHOT_REVIEW.md`):
  - **SF1** crest: face kept in hood shadow (only gold eye glint + lit nose tip); bare hand moved to mouth level with its own continuous outline, so eye / face / hand read as three layers.
  - **SF2** crest: three cut-stone mortar courses on the pillar; cowl neck curves in behind the pillar and the shoulders swell back out.
  - **SF3** `CLOSED_CHEST`: upright planked lid with iron bands and hinges, a 5-row dark cavity with warm rim, cobweb strung lid-to-rim across the open corner, moth with a dark body between its wings.
  - **SF4** `EMPTY_BIRDHOUSE`: clockwork bird perched on the right eave, brass key sticking up against the dark, beak pointing down toward the hole.
  - **SF5** `SUNDIAL` (in `scenes_b.py`): bevelled octagonal dial plate with ticks; gnomon fin with lit hypotenuse, front edge and shaded body plus cast shadow.
  - **SF6** `LEDGER_BOOKMARK` (in `scenes_b.py`): chalk trophy cup + two-coin pile outline replaces the striped columns.
  - **SF7** `KINGDOM_WARNING`: crown kept bright gold with velvet and gems, cleaved by a 2px jagged crack, right half dropped.
  - **SF8** `BadgeGrowing` + `TERRACOTTA_SPROUT`: can and pot on `l/m/k` (gold ramp), water on `a/g` (cyan); GOLD/STONE retints keep the can bright.
- **Verification**: `python tools/art/build_art.py`, `python tools/art/sprites.py`, `python tools/art/preview_sprites.py` (0 lint), `./gradlew testDebugUnitTest --rerun-tasks` → 151/151 passing.

---

## Round 2 — Owner Revision (Claude Opus `claude/claude-opus-5-5`)
- **Owner feedback** (`OWNER_FEEDBACK.md`): kingdom art should just be a castle; the background and bar backgrounds are ugly and should feel like the OSRS login-screen stone.
- **Castle of Miscellania** (`tools/art/vignettes.py` → `SceneArt.kt`): tall back keep, crenellated front gate wall with an iron portcullis, two flanking turrets with arrow slits. `KINGDOM_HEALTHY` flies a gold-and-emerald banner with lit windows and torches; `KINGDOM_WARNING` has a cracked right turret, a fallen merlon, a bent staff with a tattered crimson banner, dark windows and an alarm-red glow in the gatehouse. Advisor Ghrim/coffer art removed.
- **Login-screen masonry** (`Textures.kt`): new shared `drawAshlar` (running-bond ashlar, chamfered corners, worn chisel catch-lights, calm slab mottling, hairline fissures, optional fixed joints). `drawStoneGround` now lays monumental 11-16 × 20-36 art-px blocks with a torchlit falloff from the upper centre; `drawPanelStone` is a calmer chiselled tablet; `drawOakBeam` is clean planed timber (no knots/blots).
- **Bar & plaque chrome**: `drawOakBeamFrame` → carved stone header (two ashlar courses, iron/brass cornice, stone sill, brass rivets); `drawStoneSillFrame` → carved footer with joints on the five tab columns under a brass/iron rail; `drawPlaqueFrame` → monument tablet with end joints, double border, stepped corners and brass rivets. New `Modifier.stoneTabSlot` gives bottom tabs a raised gold-lit tablet (selected) or recessed slot (unselected), shared by `DashboardScreen` and the capture shell.
- **Constraint kept**: `ArtContrastTest` caps background luminance (OsrsRed needs 4.5:1), so the stone tone ramps stay as declared; the login-screen look comes from structure (block scale, chamfers, bevels, torchlight), not lighter stone.
- **Deviation**: footer uses brass corner rivets instead of iron L-brackets, which the new tab slots would cover.
- **Verification**: `python tools/art/build_art.py` (all grids pass contrast), `./gradlew testDebugUnitTest assembleDebug --rerun-tasks` → 151/151 passing, debug APK built. Inspected `materials.png`, `frames_plaques_ornaments.png`, `scenes.png`, `screens/02_overview.png`, `screens/17_overview_vignettes.png`.

### Round 2b — Bolder login-screen masonry (Claude Opus)
- **Shot review**: castle kept as-is (10/10); stone background and bars still too dark/low-contrast.
- **Owner green light** (`OWNER_FEEDBACK.md`, 21:29): full procedural OSRS overhaul allowed; real item/skill icons untouched.
- **Tones**: `Textures.STONE_GROUND_BASE_TONES` (PoreDark..PanelDark) are the text-safe block faces; `STONE_GROUND_TONES` now also carries OuterFrame, BorderDark, SlotRecessed, PanelLight, BorderOutline, BorderLight and HerbDark for bevels, joints and moss. `ArtContrastTest` checks text against the base faces + panel stone (text never sits on 1-2 px bevels).
- **Ground**: `drawAshlar` is now style-driven (`AshlarStyle`): 2-step chiselled top/left bevel (#5D503E/#3B3327 top, #453B2F/#352E24 left), #0C0906 joints, #120E0A undercut, worn bevel gaps, calm faces, hairline fissures, sparse #2E6B31 moss in mortar corners; blocks far from the torch keep only the inner bevel step.
- **Bars & plaque**: `Frames.BAR_ASHLAR` (2 px #5D503E + #453B2F bevel, #27211A..#352E24 faces, #120E0A undercut, #0C0906 joints); bright gold cornice rails (GoldHighlight / BrassMid / GoldShadow) on header, footer and plaque border.
- **Verification**: `./gradlew testDebugUnitTest assembleDebug --rerun-tasks` → 151/151, APK built; inspected `screens/01_setup.png`, `screens/02_overview.png`.

---

## Round 3 — Cool slate login-screen stone + new launcher crest (Claude Opus)
- **Why**: on the emulator the Round 2 stone still read as the same brown screen; the owner also could not tell what the hooded-informant icon was.
- **Slate tokens** (`Color.kt`): `OsrsSlateMortar #111315`, `OsrsSlateUndercut #1B1E21`, `OsrsSlateDark #262A2E`, `OsrsSlateMid #31363B`, `OsrsSlateLit #393F45`, `OsrsSlateBevel #4E565E`, `OsrsSlateCatchLight #636C75`, `OsrsMossDark #2E472D`, `OsrsMossLight #3E613B`. The lit face is `#393F45` rather than the requested `#3D434A` because `#3D434A` puts muted text at ~4.3:1; `#393F45` keeps it >= 4.5:1 and is visually indistinguishable.
- **Ground**: `STONE_GROUND_BASE_TONES` = slate faces; `STONE_GROUND_TONES` adds mortar, undercut, both bevel steps and moss. 2-step #636C75/#4E565E bevels, #1B1E21 undercut, #111315 mortar, fissures, moss corners and moss-covered patches on some blocks.
- **Bars / tabs / plaque**: `Frames.BAR_ASHLAR` slate faces and bevels with warm brazier-gold rails (GoldHighlight / Gold / BrassMid) and brass rivets; `stoneTabSlot` is a raised slate tablet (selected, gold catch-light) or a recessed #1B1E21 well; the title plaque is a slate monument with a gold border. Cards (`osrsPanel`) keep the warm brown stone, so the cool chrome contrasts with them.
- **Contrast tests**: panel stone still checks all five text colours at 4.5:1; slate faces check primary/muted/gold at 4.5:1 and red/herb-green (status text only) at 3:1.
- **Crest** (`tools/art/crest.py`): replaced the hooded informant with a gold-trimmed heraldic shield (crimson / royal-blue field) carrying a bold chiselled gold "P", fully inside the safe circle. A first draft had a broadsword behind it; its blade was hidden and the stray hilt read as noise, so it was dropped. Launcher background moved to slate; foreground, monochrome (outline-only cut-outs) and all legacy mipmaps regenerated; Setup crop recentred (`CREST_VIEW_X = 6`). Internal names (`INFORMANT_CREST`, `InformantCrest`) kept for call-site stability.
- **Verification**: `python tools/art/build_art.py`; `./gradlew testDebugUnitTest assembleDebug --rerun-tasks` → 152/152 passing, APK built. Inspected `screens/01_setup.png`, `02_overview.png`, `09_timers_dailies.png` and `build/art-preview/launcher.png`.

### Round 3b — Crest polish (Claude Opus)
- Shield enlarged from 14x18 to 16x21 (x 10..25, y 8..28) with a chamfered top, 2px chiselled gold border, quartered crimson/royal-blue field, bone rivets and a bolder 6x9 gold "P". The requested 18x22 rectangle (x 9..26, y 7..28) would put its top corners ~13.5 px from the centre, outside the 11 px safe circle, so 16x21 is the largest flat-topped shield that fits; `crest.build()` asserts every pixel stays inside.
- `MONO_HOLES` was already `{"Outline"}` since `ffa332c`; the dotted monochrome edge in the reviewed `launcher.png` came from an earlier render. Regenerated preview shows a solid rim with only the "P" cut out.
- Setup crest well is now recessed slate (`OsrsSlateUndercut` fill, `OsrsSlateMortar` outline, `OsrsSlateBevel` light); there is no `OsrsSlateSlot` token.
- `./gradlew testDebugUnitTest assembleDebug --rerun-tasks` → 152/152, APK built; inspected `screens/01_setup.png` and `build/art-preview/launcher.png`.
