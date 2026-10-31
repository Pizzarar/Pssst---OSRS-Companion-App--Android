# Pssst Pixel-Art Overhaul — Step 5 Shot Review (`PSSST_SHOT_REVIEW.md`)

**Author:** Gemini (`antigravity-cli/gemini-4-argon`), Step 5 SHOT REVIEW
**Inspected Sheets:**
- `app/build/art-sheet/sprites.png`
- `app/build/art-sheet/scenes.png`
- `app/build/art-sheet/frames_plaques_ornaments.png`
- `app/build/art-sheet/screens/01_setup.png`
- `app/build/art-sheet/screens/02_overview.png`
- `app/build/art-sheet/screens/11_timers_farming.png`
- `app/build/art-sheet/screens/14_empty_states.png`
- `app/build/art-sheet/screens/16_empty_vignettes.png`
- `app/build/art-sheet/screens/17_overview_vignettes.png`
- `build/art-preview/launcher.png`

---

## 1. Executive Summary

The Step 4 build is a huge leap forward over the baseline geometric boxes: all 151 unit tests pass, the `48×36` scenes and `32×24` vignettes scale with zero mixels at phone density, `FALLOW_PATCH` (grumpy Tool Leprechaun + rake + crow), `POTION_RACK` (mortar & pestle + dripping green herblore vial), `QUEST_SCROLL` (parchment pinned with Rune dagger + blue Quest Star seal), and the Slayer Sword-Through-Skull emblems (`SLAYER_ACTIVE` and `SLAYER_COMPLETE`) read cleanly at both native resolution (`scenes.png`) and `xxhdpi` phone density (`16_empty_vignettes.png`, `17_overview_vignettes.png`).

Below are **8 concrete visual fixes (`SF1`–`SF8`: 4 MUST-FIX, 4 SHOULD-FIX)** spotted directly in the exported PNGs for Step 6 (`FIX & PR`) to apply before opening the PR.

---

## 2. Visual Findings (`MUST-FIX` & `SHOULD-FIX`)

### `SF1` [MUST-FIX] — `InformantCrest`: Hand & Face Merge into a Flesh Block (`01_setup.png`, `launcher.png`)
- **Observation**: In `01_setup.png` and `launcher.png`, the bare skin-toned hand (`x = 18..20, y = 17..20`) sits directly against the skin-toned cheek/chin (`x = 16..18, y = 14..18`) with two vertical black `'o'` pixels inside the skin region. At launcher and phone size, the face + hand read as one boxy flesh snout with vertical bars rather than a shadowed face and cupped hand.
- **Fix**:
  - Keep the informant's face deeper in hood shadow (`RogueDeep` / `Outline`) so only the bright gold smirking eye glint (`GoldBright` + `ScrollLight`) and a small nose/upper-cheek highlight show inside the cowl opening.
  - Render the cupped whispering hand as a **distinct gloved hand** (using contrasting warm leather/buckskin or clearly separated by a continuous 1px `'o'` outline between the cheek and the hand) cupped at mouth level (`x = 18..21, y = 17..21`) so the eye, shadowed face, and cupped hand read as three distinct layers.

### `SF2` [MUST-FIX] — `InformantCrest`: Pillar & Hood Back Seam (`01_setup.png`, `scenes.png`)
- **Observation**: The grey stone pillar (`x = 6..9`) is a smooth vertical strip flush against the straight vertical back edge of the brown hood (`x = 10`), making the hood look like a flat brown wall glued to a grey pole.
- **Fix**:
  - Add 2–3 horizontal stone block courses (`1px` `StoneDark`/`Outline` mortar seams with `StoneLight` top edges) across the pillar shaft so it reads as cut castle masonry.
  - Curve the back of the hood/shoulder inward by 1–2px (`y = 9..14`) so the informant silhouette peeks *out from behind* the stone pillar rather than sharing a vertical 18px straight wall with it.

### `SF3` [MUST-FIX] — `CLOSED_CHEST`: Bank Chest Reads as Closed Pyramid Box Instead of Open & Empty (`scenes.png`, `16_empty_vignettes.png`)
- **Observation**: On the right side of `CLOSED_CHEST`, the lid trapezoid (`y = 11..17`) sits directly on top of the chest body (`y = 18..28`) with no dark interior cavity visible between them, so the chest still looks closed (with a tall tapered roof). Also, the moth above the chest is 4 cream pixels without a body center.
- **Fix**:
  - Carve a clear **open dark hollow interior (`y = 16..20`, `HollowDark` `#1A140E` rimmed with `HollowRim` `#4A3B2C`)** between the lower oak chest front (`y = 21..29`) and the raised open lid (`y = 10..15`), so the viewer looks directly into the empty dark chest mouth!
  - Drape the cobweb (`WebLight` / `WebMid`) across the open corner between the raised lid and the dark hollow rim, and give the fluttering moth a `1px` dark body pixel (`OakDark` / `Outline`) between its two spread wings.

### `SF4` [MUST-FIX] — `EMPTY_BIRDHOUSE`: Clockwork Bird Floating Beside Wall Instead of Perched on Roof (`scenes.png`, `16_empty_vignettes.png`)
- **Observation**: In `scenes.png` (top row, 3rd scene), the blue clockwork bird is positioned at `y = 15..22` sticking horizontally out of the right wall of the birdhouse next to the hole, and its brass wind-up key is hidden inside the roof eave.
- **Fix**:
  - Move the clockwork bird **up onto the right roof slope / eave overhang (`x = 22..32, y = 6..16`)**, clinging to the roof shingles with its **brass wind-up key (`GoldBright` / `GoldMid`) clearly visible sticking up from its back** against the dark background, and its head/beak craned downward over the eave peering into the dark entrance hole (`x = 18..23, y = 19..24`).

### `SF5` [SHOULD-FIX] — `SUNDIAL`: Gnomon Reads as a Yellow Cheese Wedge (`scenes.png`, `16_empty_vignettes.png`)
- **Observation**: Above the crisp `"RESET"` sign in `SUNDIAL`, the gnomon (`y = 4..8`) is a solid 5×5 bright yellow right-triangle sitting on a thin flat bar, reading like a wedge of cheese on a shelf.
- **Fix**:
  - Widen and bevel the top stone sundial plate (`y = 9..13`, showing an angled elliptical/octagonal stone dial face with tick dots) and refine the bronze gnomon (`y = 4..9`) into a sharper, shaded brass fin (`GoldBright` leading edge, `GoldMid` body, `GoldDark` back face) with a dark cast shadow line on the stone dial top.

### `SF6` [SHOULD-FIX] — `LEDGER_BOOKMARK`: Ghost Coin Columns Read as Skyscraper Buildings (`scenes.png`, `16_empty_vignettes.png`)
- **Observation**: In `LEDGER_BOOKMARK`, the three vertical dashed white columns (`x = 12..30, y = 3..18`) above the crimson cushion look like three lit high-rise office buildings at night because every interior row has bright horizontal dashes.
- **Fix**:
  - Replace the three dense striped towers with a cleaner **chalk-drawn ghost silhouette** of a **curved OSRS Bow (or Trophy Cup) + compact 2-stack coin pile** where only the outer contour and 1–2 top coin ovals are drawn in warm parchment chalk (`ScrollLight` / `StoneLight`), so the silhouette is immediately recognizable as treasured OSRS loot rather than office towers.

### `SF7` [SHOULD-FIX] — `KINGDOM_WARNING`: Cracked Crown Too Dark (`17_overview_vignettes.png`, `scenes.png`)
- **Observation**: In `KINGDOM_WARNING`, the tilted crown on top of the coffer is shaded in dark muddy brown (`GoldDark` / `OakDark`), so at phone size (`17_overview_vignettes.png`) it looks like a jagged brown rock rather than the Royal Crown from `KINGDOM_HEALTHY`.
- **Fix**:
  - Keep the crown in recognizable `GoldMid` / `GoldBright` with the red velvet cap (`VelvetMid`), and show the warning state via a bold **2px-wide dark jagged crack splitting the crown in two** with the right half tilted downward.

### `SF8` [SHOULD-FIX] — `BadgeGrowing`: Watering Can Contrast in `GOLD` / `STONE` Retint Rows (`sprites.png`)
- **Observation**: In `sprites.png` rows 3 (`GOLD`) and 5 (`STONE`), col 6 (`BadgeGrowing`), the watering can body uses `'a'`/`'d'` (`OsrsIronEdgeLight`/`OsrsIronBody`), which blends into the dark `#221D16` slot background when retinted to `GOLD` or `STONE`.
- **Fix**:
  - Swap the role assignment in `BadgeGrowing` and `SpriteTones.TERRACOTTA_SPROUT`: use `'l'`/`'m'`/`'k'` for the brass watering can body + spout (and set `secondaryLight = OsrsGoldHighlight`, `secondaryMid = OsrsGold`, `secondaryDark = OsrsBrassMid` on `TERRACOTTA_SPROUT`), and use `'a'`/`'g'` for the cyan water droplets (`accent = 0xFF78D4E4`, `glint = 0xFF48A8B8`). That way, in `NATURAL` it renders a gold/brass watering can + cyan water drops + green sprout, AND in `GOLD`/`STONE` retints `'l'`/`'m'`/`'k'` fall back to `highlight`/`base`/`shadow` so the watering can is bright and crisp in every row of `sprites.png`!

---

## 3. Step 6 Action Checklist (for Claude `FIX & PR`)

1. [ ] Apply `SF1` & `SF2` in `tools/art/crest.py` (separate gloved/outlined hand from shadowed face; add stone mortar courses and curve hood behind pillar).
2. [ ] Apply `SF3`, `SF4`, `SF5`, `SF6` in `tools/art/scenes.py` and `tools/art/scenes_b.py` (`CLOSED_CHEST` open dark cavity + cobweb + moth body; `EMPTY_BIRDHOUSE` clockwork bird perched on roof slope peering down into hole; `SUNDIAL` bevelled dial plate + shaded gnomon fin; `LEDGER_BOOKMARK` recognizable ghost loot silhouette instead of 3 striped towers).
3. [ ] Apply `SF7` in `tools/art/vignettes.py` (`KINGDOM_WARNING` gold crown with jagged split crack).
4. [ ] Apply `SF8` in `tools/art/sprites.py` and `PixelCanvas.kt` (`BadgeGrowing` + `TERRACOTTA_SPROUT` role swap so `GOLD`/`STONE` retint rows stay bright).
5. [ ] Re-run `python tools/art/build_art.py`, `python tools/art/sprites.py`, and `./gradlew testDebugUnitTest` to regenerate all Kotlin grids, launcher drawables/PNGs, contact sheets (`app/build/art-sheet/*.png`), and screen captures (`app/build/art-sheet/screens/*.png`).
6. [ ] Verify 100% of unit tests pass, commit all changes cleanly on `feat/piskel-art-duet` (removing temporary `build/art-preview` if untracked), push branch, and open the PR.
