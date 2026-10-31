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
