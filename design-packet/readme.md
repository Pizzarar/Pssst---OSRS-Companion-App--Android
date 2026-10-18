# Pssst Design System

**Direction in one line: "A well-worn adventurer's ledger."** Dark carved-stone panels, warm parchment text, OSRS-orange headings. Chunky bevels, no glossy gradients. It should feel like the game's interface without copying it.

## Product context
Pssst is a free, read-only, self-hosted companion for Old School RuneScape, made of two parts:

- **Pssst Companion** — a RuneLite plugin on the player's PC. It makes a snapshot of what the game already shows the player (bank, inventory, potion storage, skills, own GE offers, farming/birdhouse state, Slayer task, daily/diary progress) available to their phone.
- **Pssst** — an Android app (Jetpack Compose) that shows that state with item values, and sends notifications when timers finish.

There is no Pssst account and no central Pssst server. Data goes PC → phone directly (Local Server, same Wi-Fi or Tailscale) or through a server the player runs themselves (Pssst Worker on Cloudflare).

Surfaces covered here:
1. **Android app** — `ui_kits/android-app/` (recreated from Compose source).
2. **Setup guide (docs)** — `ui_kits/setup-guide/` (the user-facing guide outlined in `docs/setup.md`, styled per the art packet).

### Sources
- GitHub: https://github.com/Pizzarar/Pssst---OSRS-Companion-App--Android — theme in `app/src/main/java/com/pssst/theme/` (Color.kt, Theme.kt, Type.kt) is the source of truth; screens in `ui/main/` and `ui/setup/`.
- GitHub: https://github.com/Pizzarar/Pssst---OSRS-Companion-App--RL — `CompanionConfig.java` (exact setting names + descriptions), `README.md`, `docs/setup.md`.
- Local mounts used during build: `PssstAndroid/`, `PssstRunelite/`.
- "Setup docs handoff" and "Pssst art packet" briefs (pasted by Aub).

Explore those repositories further for anything this system doesn't cover — the Compose screens contain many more states than the UI kit shows.

---

## CONTENT FUNDAMENTALS
**Voice.** *Pssst* is a friend leaning over to whisper "your herbs are ready." Quiet and helpful. It shows **state and timers only** — never advice, recommendations, strategy or "you should…".

- **Person:** second person, "you/your" ("Find your Sync Token in…", "your own GE offers"). The product never says "I/we".
- **Sentences:** short, plain, one action per step. Explain *why* only where a choice matters (privacy, firewall, Sync Token).
- **Casing:** Title Case for settings, buttons, screen and card titles, used **verbatim** (`Enable Sync`, `This PC Only`, `Connect & Start Sync`, `Total Account Wealth`, `Activity Status`). UPPERCASE for state chips (`READY`, `GROWN`, `DONE`, `BUYING`). Sentence case for body copy and empty states.
- **Separators:** bullet `•` between facts ("Slot 2 • 34 / 100 @ 48.2K GP"), `→` for menu paths ("RuneLite → Configuration → Pssst Companion → Master Sync"), `~` for estimates ("Done in ~2h 14m").
- **Honesty about uncertainty:** say so plainly — "Payment amounts are unverified: they are not read from the game.", "Raw values (meaning unverified)".
- **Empty states** explain how data arrives: "No bank data synced yet. It appears after you open your bank with Pssst Companion running."
- **Warnings** state the fact, then the consequence: "127.0.0.1 is this phone, not your PC. Use your PC's address, such as http://192.168.1.20:8788".
- **Numbers:** GP as `412.6M GP`, `48.2K GP`, `2.05B GP`; stack counts as game-style `152K`, `48M`.
- **No emoji.** No exclamation marks in UI copy (notification titles like "Birdhouses ready" are fine without them in docs).
- **Banned names:** "UUID", "Auth Token", "Companion Plugin", "Loopback Only", "Enable Push", "BYOS", "Nord Meshnet". Never claim Plugin Hub / Play Store availability yet ("[link TBD]"). No "pro/premium", no comparison to other plugins, no hosted/default Pssst server.
- **Required footer on every page:** "Pssst is a free fan project and is not affiliated with or endorsed by Jagex. Old School RuneScape is a trademark of Jagex Ltd."

## VISUAL FOUNDATIONS
- **Colour.** Warm, dark, low-saturation stone (`#191612` page, `#3B3327→#2B251D` panels) with one accent, OSRS Gold `#FF981F`, for headings, step numbers, primary buttons, links and selection. Body text is parchment `#FFE0A0`, secondary `#B8A88A`. Herb Green `#4CAF50` = ready/success; Red `#FF4D4D` = warnings/errors. Stack yellow/white/green are reserved for item quantities (the app also uses yellow for value figures and the "Sync" action — keep that in-app only; docs never use it).
- **Light variant.** `[data-theme="parchment"]`: `#F2E6CC` bg, `#2B251D` ink, `#9A4A00` burnt orange. Gold on parchment fails contrast — never use it there.
- **Type.** Serif bold headings (Alegreya, standing in for the app's system Serif) at 24/20/17/14, gold with a hard 1px black offset shadow (no blur) on dark. Body is the system sans 15/22 (13/18 and 12/16 muted). JetBrains Mono for every setting name, path, URL, command and stack count. Silkscreen only for tiny badges ("NEW", "ADVANCED").
- **Surfaces.** Everything sits in a **Panel**: vertical gradient Panel Light→Panel Dark with subtle stone grain speckle, 1px `#0C0906` outer frame + 3D inner bevel (light `#5D503E` top/left, dark `#120E0A` bottom/right), optional corner hardware rivets (`rivets`), and 4px radius. **Slots** use a deep 2px top/left `#0E0B08` inset shadow + bottom/right `#514535` rim highlight and `#352E24` (`hasItem`) / `#221D16` (`empty`) fill, 3px radius — item slots, code blocks, input fields, screenshot frames.
- **Shadows.** Hard pixel bevels and 1px black text drop shadows only. No blurred drop shadows, glows, or glassmorphism.
- **Gradients.** Two-stop stone panel gradient (`#3B3327→#2B251D`), gold plaque gradient (`#FF981F→#D6760A`), and emerald ready badge/thumb gradient (`#5CD660→#388E3C`).
- **Backgrounds.** Flat Dark Stone (`#191612`) with subtle procedural stone-grain speckle on panels.
- **Borders.** 1px `#0C0906` outer frame around panels, slots, buttons, chips, and progress bars. Engraved 2px stone grooves (`#120E0A` top + `#5D503E` bottom) separate panel headers from body content. Selected sort/filter chips and active tabs get a 1px gold (`#FF981F`) border inside a sunken `Slot` well.
- **Radii.** Panel 4px, slot/tab/field/chip/button 3px, progress bar 2px. No Material pills (`20px`) or round switches.
- **Spacing.** Screen padding 16; header panels pad 14; list cards 12; tight rows 10; tab bars 4. Gaps: 12 between panels, 8 between list cards, 6 in grids. Bank grid cells min 72px; inventory 4 × 7.
- **Cards.** = Panel. Summary panels use corner rivets and an engraved header groove.
- **State colour logic.** Ready/complete → beveled green plaque + black text, green title. Waiting → recessed `Slot` badge with muted/yellow/gold text. Errors → red text + red border.
- **Progress.** Framed 8px (6px thin for skills) recessed dark-stone trough (`1px solid #0C0906`) with a 3-band shaded fill (1px top specular highlight, midtone body, 1px bottom shadow).
- **Motion.** Minimal (~120–200ms standard easing for tab/switch/drawer changes).
- **Hover/press.** Buttons invert their 3D bevel on press (`inset 2px 2px 0` shadow top/left). Selection = sunken `Slot` well + 1px gold border + gold bold text.
- **Transparency.** Used only for the drawer scrim and faded placeholder/empty-slot text.
- **Imagery & Official Item Icons.** Real screenshots framed in an inset Slot; item slots (`Bank`, `Inventory`, `Potion Storage`, `Grand Exchange`, `Dailies`, `Birdhouses`, `Farming Patches`, `Farming Payments`) always render official 36×32 OSRS item sprites (`image-rendering: pixelated`) pulled from RuneLite's static item cache (`items-complete.json`)—never procedurally generated or re-tinted substitutes.
- **Layout.** App: fixed top bar (64px) + bottom nav (80px), scrolling content. Docs: sticky header, sticky TOC sidebar, ~720px reading column.

## ICONOGRAPHY
- **App:** the Android app draws its own six line glyphs on Canvas (`DashboardScreen.kt`): menu, shield (Overview), chest (Storage), coins (Exchange), star (Progress), hourglass (Timers). Stroke ≈ 8% of size, butt caps, tinted gold (selected) or muted. Transcribed 1:1 to `assets/icons/*.svg` and the `AppIcon` component.
- **Docs:** the art packet asks for simple line icons in Text Muted (PC, phone, Wi-Fi, lock, cloud, clock, bell), 1.5–2px stroke. No set exists in the codebase, so **Lucide** (`lucide-static@0.460.0`, via CDN) is used through `DocIcon` — **substitution flagged**: Lucide has round caps; the packet prefers square ends.
- **No emoji, no unicode icon characters** (other than `•`, `→`, `~`, `×` as typographic separators).
- **Never:** skill icons, item sprites, RuneScape/Jagex/OSRS logos or lettering, RuneLite logo, stone tab icons, orbs, chatbox frames.
- **Logo:** none exists (the launcher icon is the Android Studio template). The brand is the word **Pssst** set in Alegreya ExtraBold, gold. Do not invent a mark or mascot.

## Fonts
Shipped in `assets/fonts/` (Google Fonts, OFL): Alegreya 400/500/700/800, JetBrains Mono 400/500/700, Silkscreen 400/700. Body uses the system sans stack (matches the app's `FontFamily.Default`). **Never** use Jagex game fonts or fan copies ("RuneScape UF" etc.).

---

## Index
- `styles.css` — entry point (imports only) → `tokens/fonts.css`, `colors.css`, `typography.css`, `spacing.css`, `base.css`.
- `guidelines/` — foundation specimen cards (Colors, Type, Spacing, Brand).
- `components/` — React primitives (+ `.d.ts`, `.prompt.md`, one card per folder).
- `ui_kits/android-app/` — app click-through (Setup → Overview / Storage / Exchange / Progress / Timers, drawer).
- `ui_kits/setup-guide/` — setup guide page (dark + parchment).
- `assets/icons/` — app glyph SVGs. `assets/fonts/` — webfonts.
- `SKILL.md` — agent-skill entry. `thumbnail.html` — project tile.

## Components
- **surfaces:** Panel, Slot
- **controls:** Button, TextField, Checkbox, Switch, SegmentedTabs, FilterChip
- **data:** StatusChip, ProgressBar, ItemSlot
- **navigation:** TopBar, BottomNav, DrawerItem
- **icons:** AppIcon
- **docs:** Step, StepNumber, SettingChip, CodeBlock, Callout, DocTable, ConnectionFlow, DocIcon, FanFooter

### Intentional additions
- **AppIcon** — wraps the app's Canvas-drawn glyphs so web mocks can reuse them.
- **DocIcon** — the art packet's docs icon set (Lucide substitute).
- **Step, StepNumber, SettingChip, CodeBlock, Callout, DocTable, ConnectionFlow, FanFooter** — defined by the art packet for docs; no code counterpart exists yet.
